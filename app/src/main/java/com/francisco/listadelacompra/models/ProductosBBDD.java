package com.francisco.listadelacompra.models;

import java.io.Serializable;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class ProductosBBDD implements Serializable {




    public class BaseResponse implements Serializable{

        @SerializedName("producto")
        @Expose
        private List<Producto> producto = null;

        public List<Producto> getProducto() {
            return producto;
        }

        public void setProducto(List<Producto> producto) {
            this.producto = producto;
        }

        public BaseResponse withProducto(List<Producto> producto) {
            this.producto = producto;
            return this;
        }

    }



    public class Producto implements Serializable{

        @SerializedName("_id")
        @Expose
        private String id;
        @SerializedName("name")
        @Expose
        private String name;
        @SerializedName("tipo")
        @Expose
        private String tipo;
        @SerializedName("med")
        @Expose
        private String med;
        @SerializedName("precio")
        @Expose
        private Integer precio;
        @SerializedName("__v")
        @Expose
        private Integer v;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public Producto withId(String id) {
            this.id = id;
            return this;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Producto withName(String name) {
            this.name = name;
            return this;
        }

        public String getTipo() {
            return tipo;
        }

        public void setTipo(String tipo) {
            this.tipo = tipo;
        }

        public Producto withTipo(String tipo) {
            this.tipo = tipo;
            return this;
        }

        public String getMed() {
            return med;
        }

        public void setMed(String med) {
            this.med = med;
        }

        public Producto withMed(String med) {
            this.med = med;
            return this;
        }

        public Integer getPrecio() {
            return precio;
        }

        public void setPrecio(Integer precio) {
            this.precio = precio;
        }

        public Producto withPrecio(Integer precio) {
            this.precio = precio;
            return this;
        }

        public Integer getV() {
            return v;
        }

        public void setV(Integer v) {
            this.v = v;
        }

        public Producto withV(Integer v) {
            this.v = v;
            return this;
        }

    }



}
