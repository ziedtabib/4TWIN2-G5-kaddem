import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.esprit.spring.kaddem.entities.Equipe;
import tn.esprit.spring.kaddem.entities.Niveau;
import tn.esprit.spring.kaddem.repositories.EquipeRepository;
import tn.esprit.spring.kaddem.services.EquipeServiceImpl;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EquipeServiceImplTest {

    @Mock
    private EquipeRepository equipeRepository;

    @InjectMocks
    private EquipeServiceImpl equipeService;

    @Test
    public void testAddEquipe() {
        Equipe e = new Equipe("Equipe DevOps", Niveau.JUNIOR);
        when(equipeRepository.save(any(Equipe.class))).thenReturn(e);

        Equipe result = equipeService.addEquipe(e);

        assertNotNull(result);
        assertEquals("Equipe DevOps", result.getNomEquipe());
        assertEquals(Niveau.JUNIOR, result.getNiveau());
        verify(equipeRepository, times(1)).save(e);
    }

    @Test
    public void testAddEquipeInvalid() {
        Equipe e = new Equipe(null, Niveau.JUNIOR);
        assertThrows(IllegalArgumentException.class, () -> equipeService.addEquipe(e));
    }

    @Test
    public void testRetrieveEquipe() {
        Equipe e = new Equipe(1, "Equipe Test", Niveau.SENIOR);
        when(equipeRepository.findById(1)).thenReturn(Optional.of(e));

        Equipe result = equipeService.retrieveEquipe(1);

        assertNotNull(result);
        assertEquals("Equipe Test", result.getNomEquipe());
        assertEquals(Niveau.SENIOR, result.getNiveau());
    }

    @Test
    public void testRetrieveEquipeNotFound() {
        when(equipeRepository.findById(999)).thenReturn(Optional.empty());
        Equipe result = equipeService.retrieveEquipe(999);
        assertNull(result);
    }

    @Test
    public void testDeleteEquipe() {
        Equipe e = new Equipe(2, "To Delete", Niveau.JUNIOR);
        when(equipeRepository.findById(2)).thenReturn(Optional.of(e));

        equipeService.deleteEquipe(2);

        verify(equipeRepository, times(1)).delete(e);
        // Verify equipe is no longer retrievable
        when(equipeRepository.findById(2)).thenReturn(Optional.empty());
        assertNull(equipeService.retrieveEquipe(2));
    }

    @Test
    public void testDeleteEquipeNotFound() {
        when(equipeRepository.findById(999)).thenReturn(Optional.empty());
        equipeService.deleteEquipe(999);
        verify(equipeRepository, never()).delete(any(Equipe.class));
    }

    @Test
    public void testUpdateEquipe() {
        Equipe e = new Equipe(3, "To Update", Niveau.SENIOR);
        when(equipeRepository.save(any(Equipe.class))).thenReturn(e);

        Equipe updated = equipeService.updateEquipe(e);

        assertEquals("To Update", updated.getNomEquipe());
        assertEquals(Niveau.SENIOR, updated.getNiveau());
    }
}