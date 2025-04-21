package io.reactivestax.streams;

import java.util.Comparator;
import java.util.List;

public class ProductMultipleOps {
    static class Product{
        private int id;
        private String name;
        private double price;
        private String category;

        public Product(int id, String name, double price, String category) {
            this.id = id;
            this.name = name;
            this.price = price;
            this.category = category;
        }

        public int getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public double getPrice() {
            return price;
        }

        public String getCategory() {
            return category;
        }

        @Override
        public String toString() {
            return "Product{" +
                    "id=" + id +
                    ", name='" + name + '\'' +
                    ", price=" + price +
                    ", category='" + category + '\'' +
                    '}';
        }
    }
    public static void main(String[] args) {
        List<Product> products = List.of(
                new Product(1, "Lifeboy", 20, "Soap"),
                new Product(2, "Portronics", 200, "Music"),
                new Product(3, "Monster", 300, "Drink"),
                new Product(4, "Yamaha", 5000, "Guitar"),
                new Product(5, "SurfExcel", 100, "Washing Powder")
        );

        System.out.println(products.stream().filter(product -> product.getPrice() < 100).map(Product::getName).toList());

        System.out.println(products.stream().map(product -> product.getPrice() - (product.getPrice() * 0.2)).toList());

        System.out.println(products.stream().mapToDouble(product -> product.getPrice()).average().getAsDouble());

        System.out.println(products.stream().sorted(Comparator.comparing(product -> product.getPrice())).findFirst().get());
    }
}
