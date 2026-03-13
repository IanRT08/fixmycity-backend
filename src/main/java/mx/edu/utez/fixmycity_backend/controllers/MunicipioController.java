package mx.edu.utez.fixmycity_backend.controllers;

import mx.edu.utez.fixmycity_backend.dto.request.MunicipioEstadoRequest;
import mx.edu.utez.fixmycity_backend.dto.response.ApiResponse;
import mx.edu.utez.fixmycity_backend.services.MunicipioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
public class MunicipioController {

    @Autowired
    private MunicipioService municipioService;

    @GetMapping("/api/zones/active")
    public ResponseEntity<ApiResponse> listarActivos() {

        ApiResponse response = municipioService.listarActivos();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/api/zones")
    public ResponseEntity<ApiResponse> listarTodos() {

        ApiResponse response = municipioService.listarTodos();
        return ResponseEntity.ok(response);
    }

    @PutMapping("/api/zones/{id}/estado")
    public ResponseEntity<ApiResponse> cambiarEstado(
            @PathVariable int id,
            @RequestBody @Valid MunicipioEstadoRequest request) {

        ApiResponse response = municipioService.cambiarEstado(id, request.getEstado());
        HttpStatus status = response.isSuccess() ? HttpStatus.OK : HttpStatus.BAD_REQUEST;
        return ResponseEntity.status(status).body(response);
    }
}