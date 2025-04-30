CREATE TABLE IF NOT EXISTS products_categories(
    product_id INT REFERENCES products(id) ON DELETE CASCADE,
    category_id INT REFERENCES categories(id) ON DELETE CASCADE
);