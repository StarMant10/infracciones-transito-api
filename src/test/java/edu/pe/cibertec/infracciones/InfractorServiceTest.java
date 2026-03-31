package edu.pe.cibertec.infracciones;

import edu.pe.cibertec.infracciones.model.Infractor;
import edu.pe.cibertec.infracciones.repository.InfractorRepository;
import edu.pe.cibertec.infracciones.service.impl.InfractorServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class InfractorServiceTest {

    @Mock
    private InfractorRepository infractorRepository;

    @InjectMocks
    private InfractorServiceImpl infractorService;


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
}
