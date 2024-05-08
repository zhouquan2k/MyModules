package com.progartisan.module.uiartisan;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.progartisan.component.common.Util;
import com.progartisan.component.framework.Metadata.FieldDef;
import com.progartisan.component.framework.Service;
import com.progartisan.component.meta.Meta;
import com.progartisan.component.spi.MetadataProvider;
import com.progartisan.module.misc.api.Component;
import com.progartisan.module.misc.api.UIArtisanService;
import com.progartisan.module.misc.api.Widget;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ResourceLoader;

import javax.annotation.PostConstruct;
import javax.inject.Named;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
    final private MetadataProvider metadataProvider;

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
                widget.setType(element.name);
                widget.setName(element.name);
                widget.setId(element.id);
                widget.setProperties(element.attributeMap.entrySet().stream()
                        .filter(entry -> entry.getValue().value != null)
                        .collect(Collectors.toMap(
                                entry -> entry.getKey().startsWith(":") ? entry.getKey().substring(1) : entry.getKey(),
                                entry -> entry.getValue().value)));
                if (Util.equals(element.name, "template")) {
                    element.attributes.stream().filter(attr -> attr.name.startsWith("#")).findFirst().ifPresent(attr -> {
                        widget.setName(String.format("template (%s)", attr.name.substring(1)));
                        widget.setProperty("slot", attr.name.substring(1));
                    });
                }
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
        FieldDef fieldDef = null;
        if (request.meta != null) {
            fieldDef = getFieldDefFromMeta(request.meta).clone();
            fieldDef.setName(request.meta.substring(request.meta.indexOf(".") + 1));
        }
        var newId = this._createWidget(ast, parentNode, request.type, fieldDef);
        ast.save();
        return newId;
    }

    private FieldDef getFieldDefFromMeta(String meta) {
        var entityPart = meta.substring(0, meta.lastIndexOf("."));
        var fieldPart = meta.substring(meta.lastIndexOf(".") + 1);
        if (entityPart.indexOf(".") < 0) {
            return metadataProvider.getEntityDefByName(entityPart).getFields().stream().filter(field -> Util.equals(field.getName(), fieldPart))
                    .findFirst().orElseThrow();
        } else {
            var entityPartDef = getFieldDefFromMeta(entityPart);
            return metadataProvider.getEntityDefByName(entityPartDef.getTypeName()).getFields().stream().filter(field -> Util.equals(field.getName(), fieldPart))
                    .findFirst().orElseThrow();
        }
    }

    private String _createWidget(VueAst ast, Node parentNode, String type, FieldDef meta) {
        var component = meta == null ? componentConfig.getComponent(type)
                : fromMeta(parentNode, type, meta);
        var curNode = ast.createElementWithId(parentNode, component.name);
        if (component.getProperties() != null)
            // applying default value
            component.getProperties().stream().filter(property -> property.defaultValue != null).forEach(property -> {
                if (Set.of("Boolean").contains(property.type))
                    curNode.setAttributeValue(":" + property.name, property.defaultValue);
                else
                    curNode.setAttributeValue(property.name, property.defaultValue);
            });
        if (component.child != null)
            component.child.forEach(child -> {
                _createWidget(ast, curNode, child, null);
            });
        if (component.slots != null)
            component.slots.forEach(slot -> {
                var slotNode = ast.createElement(curNode, "template");
                slotNode.setAttributeValue("#" + slot.name, "scope");
            });
        return curNode.id;
    }

    private Component fromMeta(Node parentNode, String type, FieldDef meta) {
        Element el = (Element) parentNode;
        Component ret = null;
        String requiredType = null;
        if (Util.equals(el.name, "template")) {
            var parentElement = (Element) el.parent;
            var parentComponent = componentConfig.getComponent(parentElement.name);
            var slotName = el.attributes.stream().filter(attr -> attr.name.startsWith("#")).findFirst().orElseThrow().name.substring(1);
            var slot = parentComponent.slots.stream().filter(s -> Util.equals(s.name, slotName)).findFirst().orElseThrow();
            requiredType = slot.type;
        }
        if (Util.equals(type, "el-table-column") || Util.equals(requiredType, "el-table-column")) {
            var newComponent = componentConfig.getComponent("el-table-column").clone();
            newComponent.setPropertyDefaultValue("prop", meta.getName());
            newComponent.setPropertyDefaultValue("label", meta.getLabel());
            ret = newComponent;
        } else if (Util.equals(type, "?")) {
            // guess type by meta
            // TODO event?
            if (meta.getType() == Meta.Type.String) {
                var newComponent = componentConfig.getComponent("el-input").clone();
                newComponent.setPropertyDefaultValue("placeholder", meta.getLabel());
                newComponent.setPropertyDefaultValue("v-model", "scope.data." + meta.getName());
                ret = newComponent;
            } else if (meta.getType() == Meta.Type.Enum || meta.getType() == Meta.Type.Dictionary) {
                var newComponent = componentConfig.getComponent("DictionarySelect").clone();
                newComponent.setPropertyDefaultValue("placeholder", meta.getLabel());
                newComponent.setPropertyDefaultValue("v-model", "scope.data." + meta.getName());
                newComponent.setPropertyDefaultValue("dictionary", meta.getType() == Meta.Type.Enum ? meta.getTypeName() : meta.getRefData());
                newComponent.setPropertyDefaultValue("multiple", "true");
                newComponent.setPropertyDefaultValue("clearable", "true");
                newComponent.setPropertyDefaultValue("collapse-tags", "true");
                ret = newComponent;
            } else if (meta.getType() == Meta.Type.Date || meta.getType() == Meta.Type.Timestamp) {
                var newComponent = componentConfig.getComponent("el-date-picker").clone();
                newComponent.setPropertyDefaultValue("type", "datarange");
                newComponent.setPropertyDefaultValue("range-separator", "-");
                newComponent.setPropertyDefaultValue("start-placeholder", "开始");
                newComponent.setPropertyDefaultValue("end-placeholder", "结束 " + meta.getLabel());
                newComponent.setPropertyDefaultValue("v-model", "scope.data." + meta.getName());
                ret = newComponent;
            }
            Util.check(ret != null, String.format("Component not found: meta %s", meta.getType()));
            ret.setPropertyDefaultValue("meta", meta.getName());
        }
        // type specified
        else
            ret = componentConfig.getComponent(type);

        Util.check(ret != null, String.format("Component not found: %s", type));
        return ret;
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
