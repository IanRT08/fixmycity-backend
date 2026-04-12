package mx.edu.utez.fixmycity_backend.controllers;

import mx.edu.utez.fixmycity_backend.dto.request.AsignacionReporteRequest;
import mx.edu.utez.fixmycity_backend.dto.request.CuadrillaRequest;
import mx.edu.utez.fixmycity_backend.dto.request.FinalizarReporteRequest;
import mx.edu.utez.fixmycity_backend.dto.request.SquadLeaveRequest;
import mx.edu.utez.fixmycity_backend.dto.request.VotacionRequest;
import mx.edu.utez.fixmycity_backend.dto.response.ApiResponse;
import mx.edu.utez.fixmycity_backend.security.AuthenticationHelper;
import mx.edu.utez.fixmycity_backend.services.AsignacionService;
import mx.edu.utez.fixmycity_backend.services.CuadrillaService;
import mx.edu.utez.fixmycity_backend.services.SquadVolunteerService;
import mx.edu.utez.fixmycity_backend.services.VolunteerResignService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.Valid;
import java.io.IOException;

@RestController
public class CuadrillaController {

    private final CuadrillaService cuadrillaService;
    private final AsignacionService asignacionService;
    private final SquadVolunteerService squadVolunteerService;
    private final VolunteerResignService volunteerResignService;
    private final AuthenticationHelper auth;

    public CuadrillaController(CuadrillaService cuadrillaService,
                              AsignacionService asignacionService,
                              SquadVolunteerService squadVolunteerService,
                              VolunteerResignService volunteerResignService,
                              AuthenticationHelper auth) {
        this.cuadrillaService = cuadrillaService;
        this.asignacionService = asignacionService;
        this.squadVolunteerService = squadVolunteerService;
        this.volunteerResignService = volunteerResignService;
        this.auth = auth;
    }

    @PostMapping("/api/admin/squads")
    public ResponseEntity<ApiResponse> crearCuadrilla(@RequestBody @Valid CuadrillaRequest request) {
        ApiResponse response = cuadrillaService.crearCuadrilla(request);
        return ResponseEntity.status(response.isSuccess() ? HttpStatus.CREATED : HttpStatus.BAD_REQUEST).body(response);
    }

    @GetMapping("/api/admin/squads")
    public ResponseEntity<ApiResponse> listarCuadrillas(@RequestParam(defaultValue = "activa") String estado) {
        int idAdmin = auth.getAuthenticatedUserId();
        if (idAdmin == -1) return unauthorized();
        return ResponseEntity.ok(cuadrillaService.listarCuadrillas(estado, idAdmin));
    }

    @GetMapping("/api/admin/squads/volunteers")
    public ResponseEntity<ApiResponse> voluntariosDisponibles(@RequestParam int idMunicipio) {
        return ResponseEntity.ok(cuadrillaService.listarVoluntariosDisponibles(idMunicipio));
    }

    @PutMapping("/api/admin/squads/{id}/estado")
    public ResponseEntity<ApiResponse> cambiarEstado(@PathVariable int id, @RequestParam String estado) {
        ApiResponse response = cuadrillaService.cambiarEstadoCuadrilla(id, estado);
        return ResponseEntity.status(response.isSuccess() ? HttpStatus.OK : HttpStatus.BAD_REQUEST).body(response);
    }

    @PostMapping("/api/admin/reports/assign")
    public ResponseEntity<ApiResponse> asignarReporte(@RequestBody @Valid AsignacionReporteRequest request) {
        int idAdmin = auth.getAuthenticatedUserId();
        if (idAdmin == -1) return unauthorized();
        ApiResponse response = asignacionService.asignarReporte(request, idAdmin);
        return ResponseEntity.status(response.isSuccess() ? HttpStatus.OK : HttpStatus.BAD_REQUEST).body(response);
    }

    /** Reportes asignados a la(s) cuadrilla(s) del voluntario (lista tipo ASIGNADOS). */
    @GetMapping("/api/squads/assignments")
    public ResponseEntity<ApiResponse> asignacionesCuadrilla(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false) String estado,
            @RequestParam(required = false) Integer idCuadrilla) {
        int idUsuario = auth.getAuthenticatedUserId();
        if (idUsuario == -1) return unauthorized();
        ApiResponse r = squadVolunteerService.listarAsignaciones(idUsuario, page, size, estado, idCuadrilla);
        return ResponseEntity.status(r.isSuccess() ? HttpStatus.OK : HttpStatus.BAD_REQUEST).body(r);
    }

    /** Detalle de reporte para miembro de cuadrilla (coherente con listado de asignaciones). */
    @GetMapping("/api/squads/reports/{idReporte}")
    public ResponseEntity<ApiResponse> reporteParaCuadrilla(@PathVariable int idReporte) {
        int idUsuario = auth.getAuthenticatedUserId();
        if (idUsuario == -1) return unauthorized();
        ApiResponse r = squadVolunteerService.obtenerReporteCuadrilla(idReporte, idUsuario);
        return ResponseEntity.status(r.isSuccess() ? HttpStatus.OK : HttpStatus.BAD_REQUEST).body(r);
    }

    /**
     * Baja del voluntariado y/o de una cuadrilla activa. Respuesta: {@code PerfilActualizadoResponse} (perfil + JWT nuevo).
     * Cuerpo opcional: {@code { "idCuadrilla": N }} si hay varias cuadrillas activas.
     */
    @PostMapping("/api/squads/leave")
    public ResponseEntity<ApiResponse> salirDeCuadrillaVoluntariado(
            @RequestBody(required = false) SquadLeaveRequest body) {
        int idUsuario = auth.getAuthenticatedUserId();
        if (idUsuario == -1) return unauthorized();
        String nombre = auth.getAuthenticatedUsername();
        if (nombre == null) return unauthorized();
        ApiResponse r = volunteerResignService.salir(body, idUsuario, nombre);
        if (r.isSuccess()) {
            return ResponseEntity.ok(r);
        }
        if (r.getMessage() != null && r.getMessage().contains("reportes asignados")) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(r);
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(r);
    }

    /** Cuadrilla del voluntario autenticado y sus miembros (Mi equipo). */
    @GetMapping("/api/squads/me")
    public ResponseEntity<ApiResponse> miCuadrilla() {
        int idUsuario = auth.getAuthenticatedUserId();
        if (idUsuario == -1) return unauthorized();
        ApiResponse r = squadVolunteerService.obtenerMiCuadrilla(idUsuario);
        return ResponseEntity.status(r.isSuccess() ? HttpStatus.OK : HttpStatus.BAD_REQUEST).body(r);
    }

    @PostMapping("/api/squads/vote")
    public ResponseEntity<ApiResponse> votar(@RequestBody @Valid VotacionRequest request) {
        int idUsuario = auth.getAuthenticatedUserId();
        if (idUsuario == -1) return unauthorized();
        ApiResponse response = asignacionService.registrarVoto(request, idUsuario);
        return ResponseEntity.status(response.isSuccess() ? HttpStatus.OK : HttpStatus.BAD_REQUEST).body(response);
    }

    @PutMapping("/api/squads/reports/{idReporte}/start")
    public ResponseEntity<ApiResponse> iniciarAtencion(@PathVariable int idReporte) {
        int idUsuario = auth.getAuthenticatedUserId();
        if (idUsuario == -1) return unauthorized();
        ApiResponse response = asignacionService.iniciarAtencion(idReporte, idUsuario);
        return ResponseEntity.status(response.isSuccess() ? HttpStatus.OK : HttpStatus.BAD_REQUEST).body(response);
    }

    @PutMapping("/api/squads/reports/{idReporte}/finish")
    public ResponseEntity<ApiResponse> finalizarReporte(
            @PathVariable int idReporte,
            @RequestPart("datos") @Valid FinalizarReporteRequest request,
            @RequestPart("evidencia") MultipartFile fotoEvidencia) throws IOException {
        int idUsuario = auth.getAuthenticatedUserId();
        if (idUsuario == -1) return unauthorized();
        ApiResponse response = asignacionService.finalizarReporte(idReporte, idUsuario, request, fotoEvidencia);
        return ResponseEntity.status(response.isSuccess() ? HttpStatus.OK : HttpStatus.BAD_REQUEST).body(response);
    }

    private ResponseEntity<ApiResponse> unauthorized() {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse(false, "Usuario no autenticado"));
    }
}
