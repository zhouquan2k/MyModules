package com.progartisan.module.uiartisan;

import com.progartisan.component.common.Util;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

@RequiredArgsConstructor
class VueAst {

    static class Node {
        String type = "node";
        List<Node> children = new ArrayList<Node>();
        Node parent;

        String text="";

        int level = 0;

        String serialize() {
            return text + String.join("", children.stream().map(Node::serialize).toArray(String[]::new));
        }
    }

    static class Attribute {
        String name;
        String value;
    }

    static class Event {
        String name;
        String value;
    }
    static class Element extends Node {
        String id;
        String name;
        Element(String name) {
            this.name = name;
            this.type = "element";
        }

        Map<String, Attribute> attributeMap = new HashMap<String, Attribute>();
        String getAttributeValue(String name) {
            return attributeMap.getOrDefault(name, new Attribute()).value;
        }

        void setAttributeValue(String name, String value) {
            Attribute attribute = attributeMap.get(name);
            if (attribute == null) {
                attribute = new Attribute();
                attribute.name = name;
                attributeMap.put(name, attribute);
                attributes.add(attribute);
            }
            attribute.value = value;
        }

        List<Attribute> attributes = new ArrayList<>();
        List<Event> events = new ArrayList<>();

        String serialize() {
            if (this.id != null) this.setAttributeValue("id", this.id);
            String attributeStr = this.attributes.stream().map(attribute -> {
                if (attribute.name.equals("text")) return "";
                return attribute.name + "=\"" + attribute.value + "\"";
            }).collect(Collectors.joining(" "));
            String eventStr = this.events.stream().map(event -> {
                return "@" + event.name + "=\"" + event.value + "\"";
            }).collect(Collectors.joining(" "));
            var levelSpace = "  ".repeat(this.level);
            if (Util.isNotEmpty(this.text))
                return String.format("%s<%s %s %s>%s</%s>\n", levelSpace, this.name, attributeStr, eventStr, this.text, this.name);
            return String.format("%s<%s %s %s>\n%s%s</%s>\n", levelSpace, this.name, attributeStr, eventStr, super.serialize(), levelSpace, this.name);
        }
    }

    private final String uiPath;
    private final String otherParts;
    @Setter
    @Getter
    private Node root = null; //new Node();
    private Map<String, Node> allNodes = new HashMap<String, Node>();

    private int nextId = 1;

    Node getNode(String id) {
        return allNodes.get(id);
    }

    void removeNode(String id) {
        var node = getNode(id);
        Util.check(node != null, "Node not found: " + id);
        node.parent.children.removeIf(child -> {
            if (Util.equals(child.type, "element")) {
                return ((Element) child).id.equals(id);
            }
            return false;
        });
        allNodes.remove(id);
    }

    String getNextId() {
        return "w" + this.nextId ++ ;
    }

    private void _createNode(Node parent, Node node) {
        if (parent == null) {
            Util.check(this.root == null);
            this.root = node;
        }
        else {
            parent.children.add(node);
            node.parent = parent;
            node.level = parent.level + 1;
        }
    }

    Node createNode(Node parent, String type) {
       var node = new Node();
       node.type = type;
       this._createNode(parent, node);
       return node;
    }

    Element createElement(Node parent, String tagName, String id) {
        var element =  new Element(tagName);
        this._createNode(parent, element);
        element.id = Util.isEmpty(id) ? getNextId() :id;
        allNodes.put(element.id, element);
        return element;
    }

    void visit(BiFunction<Node, Object, Object> visitFunction, Object context) {
        visit(this.root, visitFunction, context);
    }

    void visit(Node node, BiFunction<Node, Object, Object> visitFunction, Object context) {
        var newContext = visitFunction.apply(node, context);
        for (Node child : node.children) {
            visit(child, visitFunction, newContext);
        }
    }

    String serialize() {
        return this.root.serialize();
    }

    void save() throws Exception{
        var text = serialize();
        var wholeText = String.format("<template>\n%s</template>%s", text, this.otherParts);
        Files.write(Paths.get(this.uiPath), wholeText.getBytes());
    }

}
