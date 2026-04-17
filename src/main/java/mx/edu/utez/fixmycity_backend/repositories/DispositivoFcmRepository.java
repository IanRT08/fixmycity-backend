package mx.edu.utez.fixmycity_backend.repositories;

import mx.edu.utez.fixmycity_backend.modelos.DispositivoFcm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface DispositivoFcmRepository extends JpaRepository<DispositivoFcm, Long> {

    Optional<DispositivoFcm> findByTokenFcm(String tokenFcm);

    @Transactional
    void deleteByTokenFcm(String tokenFcm);

    List<DispositivoFcm> findAllByIdUsuario(int idUsuario);
}
