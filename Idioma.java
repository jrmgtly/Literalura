package com.konectape.literalura.model;

public enum Idioma {
    SPANISH("ES", "español"),
    ENGLISH("EN", "inglés"),
    FRENCH("FR", "francés"),
    PORTUGUESE("PT", "portugues");

    private String idiomaMenu;
    private String idiomaEspanol;

    Idioma(String idiomaMenu, String idiomaEspanol) {
        this.idiomaMenu = idiomaMenu;
        this.idiomaEspanol = idiomaEspanol;
    }

    public static String fromString(String idioma) {
        for (Idioma idiomaEnum : Idioma.values()) {
            if (idiomaEnum.idiomaMenu.equalsIgnoreCase(idioma)) {
                return idiomaEnum.idiomaEspanol;
            }
        }
        throw new IllegalArgumentException("Ninguna categoría encontrada");
    }
}
