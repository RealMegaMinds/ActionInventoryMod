/*
 * Copyright (C) filoghost
 *
 * SPDX-License-Identifier: MIT
 */
package me.filoghost.fcommons.command.sub.annotated;

import me.filoghost.fcommons.command.sub.SubCommand;
import me.filoghost.fcommons.command.sub.SubCommandContext;
import me.filoghost.fcommons.command.validation.CommandException;
import org.bukkit.command.CommandSender;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AnnotatedMethodSubCommand extends AnnotatedSubCommand {

    private static final Map<Class<?>, MethodParameterProvider> parameterProviders = new HashMap<>();
    static {
        parameterProviders.put(SubCommandContext.class, context -> context);
        parameterProviders.put(CommandSender.class, SubCommandContext::getSender);
        parameterProviders.put(SubCommand.class, SubCommandContext::getSubCommand);
        parameterProviders.put(String[].class, SubCommandContext::getArgs);
    }

    private final Object instance;
    private final Method method;
    private final List<MethodParameterProvider> methodParameterProviders;

    protected AnnotatedMethodSubCommand(Object instance, Method method) {
        this.instance = instance;
        this.method = method;
        this.method.setAccessible(true);

        super.readPropertiesFromAnnotations();

        this.methodParameterProviders = Arrays.stream(method.getParameterTypes()).map((Class<?> paramType) -> {
            MethodParameterProvider parameterProvider = parameterProviders.get(paramType);
            if (parameterProvider == null) {
                throw new IllegalArgumentException("Method " + method.getName()
                        + " contains unsupported parameter type: " + paramType.getSimpleName());
            }
            return parameterProvider;
        }).collect(Collectors.toList());
    }

    @Override
    protected AnnotatedElement getAnnotatedElement() {
        return method;
    }

    @Override
    public void execute(CommandSender sender, String[] args, SubCommandContext context) throws CommandException {
        Object[] methodParameters = methodParameterProviders.stream().map(provider -> provider.get(context)).toArray();

        try {
            method.invoke(instance, methodParameters);
        } catch (InvocationTargetException e) {
            if (e.getCause() instanceof CommandException) {
                throw (CommandException) e.getCause();
            } else {
                throw new RuntimeException(e.getCause());
            }
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
    }


    private interface MethodParameterProvider {

        Object get(SubCommandContext context);

    }

}
