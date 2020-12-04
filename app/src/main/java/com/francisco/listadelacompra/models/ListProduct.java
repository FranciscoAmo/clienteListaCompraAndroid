package com.francisco.listadelacompra.models;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;


public class ListProduct implements Serializable {

    public class AssociatedUser implements Serializable{

        @SerializedName("_id")
        @Expose
        private String id;
        @SerializedName("email")
        @Expose
        private String email;
        @SerializedName("displayName")
        @Expose
        private String displayName;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getDisplayName() {
            return displayName;
        }

        public void setDisplayName(String displayName) {
            this.displayName = displayName;
        }

    }


    public class BaseList implements Serializable{

        @SerializedName("message")
        @Expose
        private String message;
        @SerializedName("list")
        @Expose
        private java.util.List<List> list = null;

        public java.util.List<List> getList() {
            return list;
        }

        public void setList(java.util.List<List> list) {
            this.list = list;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }


    public class List implements Serializable{

        @SerializedName("associatedUsers")
        @Expose
        private java.util.List<AssociatedUser> associatedUsers = null;
        @SerializedName("createdDate")
        @Expose
        private String createdDate;
        @SerializedName("_id")
        @Expose
        private String id;
        @SerializedName("nameList")
        @Expose
        private String nameList;
        @SerializedName("products")
        @Expose
        private java.util.List<Product> products = null;
        @SerializedName("__v")
        @Expose
        private Integer v;

        public java.util.List<AssociatedUser> getAssociatedUsers() {
            return associatedUsers;
        }

        public void setAssociatedUsers(java.util.List<AssociatedUser> associatedUsers) {
            this.associatedUsers = associatedUsers;
        }

        public String getCreatedDate() {
            return createdDate;
        }

        public void setCreatedDate(String createdDate) {
            this.createdDate = createdDate;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getNameList() {
            return nameList;
        }

        public void setNameList(String nameList) {
            this.nameList = nameList;
        }

        public java.util.List<Product> getProducts() {
            return products;
        }

        public void setProducts(java.util.List<Product> products) {
            this.products = products;
        }

        public Integer getV() {
            return v;
        }

        public void setV(Integer v) {
            this.v = v;
        }

    }


    public class Product implements Serializable{

        @SerializedName("quantity")
        @Expose
        private Integer quantity;
        @SerializedName("product")
        @Expose
        private Product_ product;

        public Integer getQuantity() {
            return quantity;
        }

        public void setQuantity(Integer quantity) {
            this.quantity = quantity;
        }

        public Product_ getProduct() {
            return product;
        }

        public void setProduct(Product_ product) {
            this.product = product;
        }

    }


    public class Product_ implements Serializable{

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

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getTipo() {
            return tipo;
        }

        public void setTipo(String tipo) {
            this.tipo = tipo;
        }

        public String getMed() {
            return med;
        }

        public void setMed(String med) {
            this.med = med;
        }

        public Integer getPrecio() {
            return precio;
        }

        public void setPrecio(Integer precio) {
            this.precio = precio;
        }

    }
}