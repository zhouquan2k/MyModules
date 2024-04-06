package com.progartisan.module.uiartisan;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.progartisan.component.common.Util;
import com.progartisan.component.framework.Service;
import com.progartisan.module.misc.api.Component;
import com.progartisan.module.misc.api.UIArtisanService;
import com.progartisan.module.misc.api.Widget;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ResourceLoader;

import javax.annotation.PostConstruct;
import javax.inject.Named;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.progartisan.module.uiartisan.VueAst.Element;
import static com.progartisan.module.uiartisan.VueAst.Node;

@Named
@RequiredArgsConstructor
@Service(type = Service.Type.Mixed, name = "ui-artisan", order = -1)
public class UIArtisanServiceImpl implements UIArtisanService {

    ComponentConfig componentConfig;

    final private ResourceLoader resourceLoader;
    final private Parser parser;

    static class ParseContext {
        Widget parent = new Widget();
    }

    @Override
    public Widget getWidgetTree(String vueFilePath) throws Exception {
        VueAst vue = parser.parse(vueFilePath);
        var rootWidget = new Widget();
        vue.visit((node, parent) -> {
            // convert element to widget
            if (Util.equals(node.type, "element")) {
                Element element = (Element) node;
                Widget widget = new Widget();
                widget.setName(element.name);
                widget.setId(element.id);
                widget.setProperties(element.attributeMap.entrySet().stream()
                        .filter(entry -> entry.getValue().value != null)
                        .collect(Collectors.toMap(
                                entry -> entry.getKey().startsWith(":") ? entry.getKey().substring(1) : entry.getKey(),
                                entry -> entry.getValue().value)));
                ((Widget) parent).getChildren().add(widget);
                parent = widget;
            }
            return parent;
        }, rootWidget);
        return rootWidget;
    }

    public void initUI(String vueFilePath) throws Exception {
        VueAst ast = parser.parse(vueFilePath);
        String text = ast.serialize();
        System.out.println(text);
        // TODO save to file
    }

    @PostConstruct
    public void init() {
        try {
            this.componentConfig = loadComponentConfig("classpath:/components.yml");
            this.componentConfig.init();
        } catch (Exception e) {
            throw new RuntimeException("init", e);
        }
    }

    private ComponentConfig loadComponentConfig(String configFile) throws Exception {
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        return mapper.readValue(this.resourceLoader.getResource(configFile).getInputStream(), ComponentConfig.class);
    }

    @Override
    public List<Component> getComponents() {
        return this.componentConfig.components;
    }

    @Override
    public String createWidget(String vueFilePath, String id, CreateWidgetRequest request) throws Exception {
        VueAst ast = parser.parse(vueFilePath);
        var parentNode = ast.getNode(id);
        Util.check(parentNode != null, "Parent node not found: " + id);
        var newId = this._createWidget(ast, parentNode, request.type);
        ast.save();
        return newId;
    }

    private String _createWidget(VueAst ast, Node parentNode, String type) {
        var component = componentConfig.getComponent(type);
        var curNode = ast.createElementWithId(parentNode, type);
        if (component.properties != null)
            component.properties.stream().filter(property -> property.defaultValue != null).forEach(property -> {
                curNode.setAttributeValue(property.name, property.defaultValue);
            });
        if (component.child != null)
            component.child.forEach(child -> {
                _createWidget(ast, curNode, child);
            });
        if (component.slots != null)
            component.slots.forEach(slot -> {
                var slotNode = ast.createElement(curNode, "template");
                slotNode.setAttributeValue("#" + slot, null);
            });
        return curNode.id;
    }

    // 问题：text没有id，无法定位更新，只能作为widget的属性
    // 解决：将单个非空子的node作为text属性处理，其他不支持编辑器修改，除非加上<span></span>变成单个非空子
    @Override
    public void updateWidget(String vueFilePath, String id, Map<String, Object> params) throws Exception {
        VueAst ast = parser.parse(vueFilePath);
        Node node = ast.getNode(id);
        Util.check(node != null, "Node not found: " + id);
        if (node instanceof Element) {
            Element element = (Element) node;
            for (Map.Entry<String, Object> entry : params.entrySet()) {
                var property = componentConfig.getProperty(element.name, entry.getKey());
                if (entry.getKey().equals("text")) {
                    // TODO using meta
                    element.text = entry.getValue().toString();
                }
                else {
                    var attrName = Util.equals(property.type, "String") ? entry.getKey() : ":" + entry.getKey();
                    element.setAttributeValue(attrName, entry.getValue().toString());
                }
            }
        }
        ast.save();
    }

    @Override
    public void deleteWidget(String vueFilePath, String id) throws Exception {
        VueAst ast = parser.parse(vueFilePath);
        ast.removeNode(id);
        ast.save();
    }
}
