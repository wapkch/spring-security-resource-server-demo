package io.github.cathy.controller;

import brave.propagation.ExtraFieldPropagation;

import io.micrometer.tracing.Baggage;
import io.micrometer.tracing.Tracer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {

    @Autowired
    private Tracer tracer;

    @PreAuthorize("hasAuthority('perm1')")
    @GetMapping(path = "/home")
    public String home() {
        return "Hello, World!";
    }


}
