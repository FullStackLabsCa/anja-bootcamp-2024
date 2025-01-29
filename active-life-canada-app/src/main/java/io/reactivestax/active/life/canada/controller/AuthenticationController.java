package io.reactivestax.active.life.canada.controller;

import io.reactivestax.active.life.canada.constant.Endpoints;
import io.reactivestax.active.life.canada.dto.RegisterMemberRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController(Endpoints.BASE)
public class AuthenticationController {

    @PostMapping(Endpoints.SIGNUP)
    private void signUp(@RequestBody RegisterMemberRequest registerMemberRequest){


    }
}
