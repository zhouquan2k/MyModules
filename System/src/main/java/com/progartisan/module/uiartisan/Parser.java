package com.progartisan.module.uiartisan;

import com.progartisan.component.common.Util;
import com.progartisan.module.uiartisan.antlr.VueParser;
import com.progartisan.module.uiartisan.antlr.XMLLexer;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

import javax.inject.Named;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Map;
import java.util.stream.Stream;

import static com.progartisan.module.uiartisan.VueAst.*;

@Named
class Parser {
    static Map<String, String> rootPaths = Map.of("@user", "/Users/zhouquan/Workspace/MyModules/System/webfrontend/user"
            , "@gcp", "/Users/zhouquan/Workspace/GCP/webfrontend");

    VueAst parse(String vueFilePath) throws Exception {
        vueFilePath = vueFilePath.replace('|','/');
        var prefix = vueFilePath.substring(0, vueFilePath.indexOf("/"));
        var fullPath = rootPaths.get(prefix) + vueFilePath.substring(prefix.length());
        // 1.open vue file ,extract template part of vue file
        String content = Files.readString(Paths.get(fullPath));
        var startMark = "<template>";
        var endMark = "</template>";
        var start = content.indexOf(startMark) + startMark.length();
        var end = content.lastIndexOf(endMark);
        String templateContent = content.substring(start, end);
        String otherParts = content.substring(content.lastIndexOf(endMark) + endMark.length());

        ANTLRInputStream input = new ANTLRInputStream(templateContent);
        var lexer = new XMLLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        VueParser parser = new VueParser(tokens);
        ParseTree tree = parser.content();
        System.out.println(tree.toStringTree(parser));

        // AtomicInteger idSerial = new AtomicInteger(0);
        var ast = new VueAst(fullPath, otherParts);
        parseToAst(tree, null, ast);
        // ast.setRoot(root);
        return ast;
    }

    private String getStringValue(String str) {
        if (str.startsWith("\"") && str.endsWith("\"")) {
            return str.substring(1, str.length() - 1);
        }
        return null;
    }

    private Node parseToAst(ParseTree node, Node parent, VueAst ast) {
        Node astNode = null;
        if (node instanceof VueParser.ElementContext) {
            var element = (VueParser.ElementContext) node;
            var name = element.Name(0).getText();
            // Element astElement = new Element(name);
            var attributes = Util.toList(element.attribute().stream().flatMap(attribute -> {
                var attr = new VueAst.Attribute();
                if (attribute.attributeName() != null) {
                    attr.name = attribute.attributeName().getText();
                    attr.value = (attribute.attributeValue() != null) ? getStringValue(attribute.attributeValue().getText()) : null;
                } else if (attribute.sharpName() != null) {
                    attr.name = "#" + attribute.sharpName().getText();
                    attr.value = (attribute.sharpValue() != null) ? getStringValue(attribute.sharpValue().getText()) : null;
                }
                else {
                    return Stream.empty();
                }
                return Stream.of(attr);
            }));
            var attributeMap = Util.toMap(attributes.stream(), attribute -> attribute.name, attribute -> attribute);
            var id = attributeMap.getOrDefault("id", new Attribute()).value;
            Element astElement = ast.createElement(parent, name, id);
            astElement.attributes = attributes;
            astElement.attributeMap = attributeMap;
            astElement.events = Util.toList(element.attribute().stream().flatMap(attribute -> {
                if (attribute.eventName() != null) {
                    var event = new Event();
                    event.name = attribute.eventName().getText();
                    event.value = getStringValue(attribute.eventValue().getText());
                    return Stream.of(event);
                }
                return Stream.empty();
            }));
            astNode = astElement;
            if (element.content() != null) {
                var childCount = element.content().getChildCount();
                for (int i = 0; i < childCount; i++) {
                    var child = element.content().getChild(i);
                    var childNode = parseToAst( child, astElement, ast);
                    if (element.content().getChildCount() == 1 && childNode.type.equals("node") && childNode.text!= null) {
                        astElement.text = (childNode.text != null)? childNode.text.trim() : null;
                        astElement.children.remove(childNode);
                    }
                }
            }
            if (Util.isNotEmpty(astElement.text)) {
                astElement.setAttributeValue("text", astElement.text);
            }
        }
        else {
            astNode = ast.createNode(parent, "node");
            var childCount = node.getChildCount();
            if (node instanceof VueParser.ChardataContext) {
                if (node.getText()!=null && Util.isNotEmpty(node.getText().trim()))
                    astNode.text = node.getText();
            }
            else if (childCount > 0) {
                astNode.children = new ArrayList<>();
                for (int i = 0; i < node.getChildCount(); i++) {
                    var child = node.getChild(i);
                    var childNode = parseToAst(child, astNode, ast);
                    if (childNode.type.equals("node") && childNode.text == null) {
                        astNode.children.remove(childNode);
                    }
                }
            }
            else
                astNode.text = node.getText();
        }
        return astNode;
    }
}

