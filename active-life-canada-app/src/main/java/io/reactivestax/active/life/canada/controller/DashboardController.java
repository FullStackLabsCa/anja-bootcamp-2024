package io.reactivestax.active.life.canada.controller;

import io.reactivestax.active.life.canada.constant.Endpoints;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(Endpoints.BASE_ENDPOINT + Endpoints.DASHBOARD)
public class DashboardController {
    @GetMapping
    public void dashboard() {

    }
}
