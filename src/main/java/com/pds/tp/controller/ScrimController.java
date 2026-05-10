package com.pds.tp.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController("v1/api/scrims")
public class ScrimController {

    @GetMapping("/")
    public ResponseEntity find(@RequestParam String juego,
                               @RequestParam String region,
                               @RequestParam String rangoMin,
                               @RequestParam String rangoMax,
                               @RequestParam String fecha,
                               @RequestParam String latenciaMax) {
        return ResponseEntity.ok().build();
    }

    @PostMapping("/")
    public ResponseEntity create() {
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/postulaciones")
    public ResponseEntity apply(@PathVariable String id) {
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/confirmaciones")
    public ResponseEntity confirm(@PathVariable String id) {
        return ResponseEntity.ok().build();
    }

/*    @PostMapping("/{id}/acciones/{command}")
    public ResponseEntity test(@PathVariable String id,
                               @PathVariable String command) {
        return ResponseEntity.ok().build();
    }*/

    @PostMapping("/{id}/cancelar")
    public ResponseEntity cancel(@PathVariable String id) {
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/finalizar")
    public ResponseEntity end(@PathVariable String id) {
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}/estadisticas")
    public ResponseEntity stats(@PathVariable String id) {
        return ResponseEntity.ok().build();
    }
}
