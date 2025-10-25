package ar.edu.iua.iw3.gastrack.util;

import java.text.DecimalFormat;
import java.util.Random;

/**
 * Clase utilitaria para la generacion de contraseñas de activacion
 * 
 * @author Leandro Biondi
 * @author Benjamin Vargas
 * @author Antonella Badami
 * @version 1.0
 * @since 2025-10-25
 */

public final class ContrasenaActivacionUtiles {

    /**
     * Genera una contraseña numérica aleatoria de 5 dígitos.
     *
     * @return Contraseña generada en formato de cadena con ceros a la izquierda si es necesario.
     */
    public static String generarContrasena (){
        int limite = 100000;
        Random random = new Random();
        int num = random.nextInt(limite);
        DecimalFormat decimalFormat = new DecimalFormat("00000");

        return decimalFormat.format(num);
    }

}
