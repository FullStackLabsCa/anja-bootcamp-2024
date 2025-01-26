package io.reactivestax.message.processor.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TwiMLController {
    @GetMapping(value = "/generate-twiml/{message}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> generateTwiml(@PathVariable String message) {
        // Build the TwiML XML with the custom message
        String twimlXml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
                + "<Response>"
                + "<Say voice=\"alice\" language=\"en-US\">" + message + "</Say>"
                + "</Response>";

        return ResponseEntity.ok().body(twimlXml);
    }
}