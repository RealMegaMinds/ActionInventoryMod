/*
 * Copyright (C) filoghost
 *
 * SPDX-License-Identifier: MIT
 */
package me.filoghost.fcommons.command.sub.annotated;

import me.filoghost.fcommons.command.ConfigurableCommandProperties;
import me.filoghost.fcommons.command.sub.SubCommand;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;

public abstract class AnnotatedSubCommand extends ConfigurableCommandProperties implements SubCommand {

    private int displayPriority;
    private String description;

    protected AnnotatedElement getAnnotatedElement() {
        return null;
    }

    public <T extends Annotation> T getAnnotation(Class<T> annotationClass) {
        AnnotatedElement annotatedElement = getAnnotatedElement();
        if (annotatedElement == null) {
            return null;
        }

        return annotatedElement.getAnnotation(annotationClass);
    }

    protected void readPropertiesFromAnnotations() {
        AnnotatedElement annotatedElement = getAnnotatedElement();
        if (annotatedElement == null) {
            return;
        }

        Name name = annotatedElement.getAnnotation(Name.class);
        if (name != null) {
            setName(name.value());
        }

        Permission permission = annotatedElement.getAnnotation(Permission.class);
        if (permission != null) {
            setPermission(permission.value());
        }

        PermissionMessage permissionMessage = annotatedElement.getAnnotation(PermissionMessage.class);
        if (permissionMessage != null) {
            setPermissionMessage(permissionMessage.value());
        }

        UsageArgs usage = annotatedElement.getAnnotation(UsageArgs.class);
        if (usage != null) {
            setUsageArgs(usage.value());
        }

        MinArgs minArgs = annotatedElement.getAnnotation(MinArgs.class);
        if (minArgs != null) {
            setMinArgs(minArgs.value());
        }

        DisplayPriority displayPriority = annotatedElement.getAnnotation(DisplayPriority.class);
        if (displayPriority != null) {
            setDisplayPriority(displayPriority.value());
        }

        Description description = annotatedElement.getAnnotation(Description.class);
        if (description != null) {
            setDescription(description.value());
        }
    }

    public int getDisplayPriority() {
        return displayPriority;
    }

    public void setDisplayPriority(int displayPriority) {
        this.displayPriority = displayPriority;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}
