package com.project.ecommerce.contactmessage.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDateTime;

/*
 	 Nesnelerimiz Ag ortaminda degerleri ile berbaer gonderilecekse bu yapi kullanlir
 ama RESTful API'lar genellikle JSON veya XML formatında veri
 alışverişi yapar. Bu tür serileştirme işlemleri, Java'nın yerleşik Serializable
 arayüzünden bağımsız olarak çalışır. Spring Boot gibi modern frameworkler, Jackson
 veya Gson gibi kütüphaneleri kullanarak nesneleri JSON'a otomatik olarak serileştirir
 ve deserializasyon yapar. Bu süreç, Java'nın yerel serileştirme mekanizmasından
 farklıdır. RESTful servislerde genellikle nesnelerin durumları JSON olarak iletilir
 ve bu süreçte Java'nın yerel serileştirme mekanizmasına ihtiyaç duyulmaz.
 */ //!!! SERIALIZABLE ACIKLAMA
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)

@Entity
public class ContactMessage implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // contactId , contactMessageId

    @NotNull
    private String name;

    @NotNull
    private String email;

    @NotNull
    private String subject;

    @NotNull
    private String message;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm", timezone = "US")
    private LocalDateTime dateTime;
}
