package ru.andef.andefracing.backend.domain.mappers.club;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import ru.andef.andefracing.backend.data.entities.club.Photo;
import ru.andef.andefracing.backend.network.dtos.common.PhotoDto;
import ru.andef.andefracing.backend.network.dtos.management.AddPhotoDto;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface PhotoMapper {
    @Mapping(target = "id", expression = "java(photo.getId())")
    @Mapping(target = "url", expression = "java(photo.getUrl())")
    @Mapping(target = "sequenceNumber", expression = "java(photo.getSequenceNumber())")
    PhotoDto toDto(Photo photo);

    List<PhotoDto> toDto(List<Photo> photos);

    @Mapping(target = "url", expression = "java(photo.url())")
    @Mapping(target = "sequenceNumber", expression = "java(photo.sequenceNumber())")
    Photo toEntity(AddPhotoDto addPhotoDto);
}
