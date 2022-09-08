package com.androiddev.shoesfit.model;

import java.util.HashMap;
import java.util.Map;

public class Filter {

    public enum KEBUTUHAN{
        TIPE_MENDAKI(0),
        TIPE_OLAHRAGA(1),
        TIPE_RAPAT_FORMAL(2),
        TIPE_PESTA(3);

        private int value;
        private static Map map = new HashMap<>();

        private KEBUTUHAN(int value) {
            this.value = value;
        }

        static {
            for (KEBUTUHAN kebutuhan : KEBUTUHAN.values()) {
                map.put(kebutuhan.value, kebutuhan);
            }
        }

        public static KEBUTUHAN valueOf(int kebutuhan) {
            return (KEBUTUHAN) map.get(kebutuhan);
        }

        public int getValue() {
            return value;
        }
    }

    public enum HARGA{
        DI_BAWAH_200K(0),
        ANTARA_200K_SAMPAI_300K(1),
        DI_ATAS_300K(2);

        private int value;
        private static Map map = new HashMap<>();

        private HARGA(int value) {
            this.value = value;
        }

        static {
            for (HARGA harga : HARGA.values()) {
                map.put(harga.value, harga);
            }
        }

        public static HARGA valueOf(int harga) {
            return (HARGA) map.get(harga);
        }

        public int getValue() {
            return value;
        }
    }

    public enum CELANA{
        NAVY(0),
        BLACK(1),
        BLUE(2),
        LIGHT_GREY(3),
        DARK_GREY(4),
        WHITE(5),
        TAN(6),
        BEIGE(7),
        BROWN(8),
        OLIVE(9),
        JEANS(10);

        private int value;
        private static Map map = new HashMap<>();

        private CELANA(int value) {
            this.value = value;
        }

        static {
            for (CELANA celana : CELANA.values()) {
                map.put(celana.value, celana);
            }
        }

        public static CELANA valueOf(int celana) {
            return (CELANA) map.get(celana);
        }

        public int getValue() {
            return value;
        }
    }

    KEBUTUHAN kebutuhan;
    CELANA celana;
    HARGA harga;

    public Filter(KEBUTUHAN kebutuhan, CELANA celana, HARGA harga) {
        this.kebutuhan = kebutuhan;
        this.celana = celana;
        this.harga = harga;
    }

    public KEBUTUHAN getKebutuhan() {
        return kebutuhan;
    }

    public void setKebutuhan(KEBUTUHAN kebutuhan) {
        this.kebutuhan = kebutuhan;
    }

    public CELANA getCelana() {
        return celana;
    }

    public void setCelana(CELANA celana) {
        this.celana = celana;
    }

    public HARGA getHarga() {
        return harga;
    }

    public void setHarga(HARGA harga) {
        this.harga = harga;
    }
}
