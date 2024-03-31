package com.progartisan.module.uiartisan;

import com.progartisan.component.common.Util;
import com.progartisan.module.misc.api.Component;
import com.progartisan.module.misc.api.Component.Property;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

class ComponentConfig {
    public List<Component> components;

    private Map<String, Component> componentMap = new HashMap<>();

    public void init() {
        componentMap = components.stream().collect(Collectors.toMap(c -> c.name, c -> c));
    }

    public Component getComponent(String name) {
        return componentMap.get(name);
    }

    public Property getProperty(String name, String propertyName) {
        Component component = getComponent(name);
        Util.check(component != null, "Component not found: " + name);
        return component.properties.stream().filter(p -> p.name.equals(propertyName)).findFirst().orElseThrow();
    }
}
