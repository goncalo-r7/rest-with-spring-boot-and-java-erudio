package pt.goncalorodrigues.exceptions;

import java.util.Date;

public record ExceptionResponse(Date timestamp, String message, String details) {
    // record -> simplifica a criação de classes que vão apenas armazenar valores
    // se classe não será alterada depois de criada -> record

}
