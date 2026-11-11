package dominio;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class NivelAlertaTest {

    private static final double LIMIAR = 30;

    @Test
    void deveClassificarComoNormalAbaixoDe80Porcento() {
        // Arrange
        double previsto = 20;

        // Act
        NivelAlerta nivel = NivelAlerta.classificar(previsto, LIMIAR);

        // Assert
        assertEquals(NivelAlerta.NORMAL, nivel);
    }

    @Test
    void deveClassificarComoAtencaoAPartirDe80Porcento() {
        // Arrange: 24 / 30 = 80%
        double previsto = 24;

        // Act
        NivelAlerta nivel = NivelAlerta.classificar(previsto, LIMIAR);

        // Assert
        assertEquals(NivelAlerta.ATENCAO, nivel);
    }

    @Test
    void deveClassificarComoAlertaAoAtingirOLimiar() {
        // Arrange: 30 / 30 = 100%
        double previsto = 30;

        // Act
        NivelAlerta nivel = NivelAlerta.classificar(previsto, LIMIAR);

        // Assert
        assertEquals(NivelAlerta.ALERTA, nivel);
    }

    @Test
    void deveClassificarComoCriticoAcimaDe150Porcento() {
        // Arrange: 48 / 30 = 160%
        double previsto = 48;

        // Act
        NivelAlerta nivel = NivelAlerta.classificar(previsto, LIMIAR);

        // Assert
        assertEquals(NivelAlerta.CRITICO, nivel);
    }

    @Test
    void deveClassificarComoCriticoExatamenteEm150Porcento() {
        // Arrange: 45 / 30 = 150%
        double previsto = 45;

        // Act
        NivelAlerta nivel = NivelAlerta.classificar(previsto, LIMIAR);

        // Assert
        assertEquals(NivelAlerta.CRITICO, nivel);
    }
}