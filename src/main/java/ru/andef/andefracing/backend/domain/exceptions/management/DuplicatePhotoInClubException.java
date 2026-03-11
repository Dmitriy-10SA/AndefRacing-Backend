package ru.andef.andefracing.backend.domain.exceptions.management;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Ошибка - клуб уже содержит фото с таким url или sequenceNumber
 */
@Getter
@ResponseStatus(HttpStatus.CONFLICT)
public class DuplicatePhotoInClubException extends RuntimeException {
    private String url = null;
    private Short sequenceNumber = null;
    private final boolean isUrlException;
    private final boolean isSequenceNumberException;

    public DuplicatePhotoInClubException(String url) {
        super("Фото с url" + url + " уже существует в клубе");
        this.url = url;
        this.isUrlException = true;
        this.isSequenceNumberException = false;
    }

    public DuplicatePhotoInClubException(short sequenceNumber) {
        super("Фото с sequenceNumber" + sequenceNumber + " уже существует в клубе");
        this.sequenceNumber = sequenceNumber;
        this.isUrlException = false;
        this.isSequenceNumberException = true;
    }
}
