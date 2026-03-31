package edu.pe.cibertec.infracciones;

import edu.pe.cibertec.infracciones.model.EstadoMulta;
import edu.pe.cibertec.infracciones.model.Infractor;
import edu.pe.cibertec.infracciones.model.Multa;
import edu.pe.cibertec.infracciones.repository.InfractorRepository;
import edu.pe.cibertec.infracciones.repository.MultaRepository;
import edu.pe.cibertec.infracciones.service.impl.InfractorServiceImpl;
import edu.pe.cibertec.infracciones.service.impl.MultaServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class InfractorServiceTest {

    @Mock
    private InfractorRepository infractorRepository;

    @Mock
    private MultaRepository multaRepository;

    @InjectMocks
    private InfractorServiceImpl infractorService;

    @InjectMocks
    private MultaServiceImpl multaService;


    @Test
    @DisplayName("No bloquea cuando tiene 2 multas vencidas")
    void noBloqueaConDosMultasVencidas() {

        Long infractorId = 1L;

        Infractor infractor = new Infractor();
        infractor.setId(infractorId);
        infractor.setBloqueado(false);

        when(infractorRepository.findById(infractorId))
                .thenReturn(Optional.of(infractor));

        when(infractorRepository.countMultasVencidasByInfractorId(infractorId))
                .thenReturn(2);

        assertDoesNotThrow(() -> infractorService.verificarBloqueo(infractorId));


        assertFalse(infractor.isBloqueado());

        verify(infractorRepository, never()).save(any());
    }

    @Test
    @DisplayName("Cambia estado de multa a VENCIDA cuando la fecha de vencimiento ya pasó")
    void cambiaEstadoAVencidaCuandoFechaExpira() {

        Multa multa = new Multa();
        multa.setEstado(EstadoMulta.valueOf("PENDIENTE"));
        multa.setFechaVencimiento(LocalDate.of(2026, 1, 1));

        when(multaRepository.findByEstadoAndFechaVencimientoBefore(eq("PENDIENTE"), any()))
                .thenReturn(List.of(multa));

        multaService.actualizarEstados();

        assertEquals(EstadoMulta.VENCIDA, multa.getEstado());

        verify(multaRepository).saveAll(any());
    }
}
