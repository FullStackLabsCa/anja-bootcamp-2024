INSERT INTO category (name, description) VALUES
('Sports', 'All sports-related activities'),
('Music', 'Music and instrument learning');

SELECT * FROM category;

INSERT INTO sub_category (name, description, category_id) VALUES
('Football', 'Learn football techniques', 1),
('Piano', 'Master the piano', 2);

SELECT * FROM sub_category;

INSERT INTO age_group (short_code, description) VALUES
('KIDS', 'For children aged 5-10'),
('TEENS', 'For teenagers aged 11-17'),
('ADULTS', 'For adults 18+');

SELECT * FROM age_group;

INSERT INTO course (name, description, sub_category_id, age_group_id) VALUES
('Beginner Football', 'Football for beginners', 1, 1),
('Advanced Football', 'Football for experienced players', 1, 2),
('Basic Piano', 'Introduction to piano playing', 2, 1);

SELECT * FROM course;

INSERT INTO facility (name, street_no, street_name, city, province, country, postal_code, description) VALUES
('City Sports Center', '12', 'Main St', 'New York', 'NY', 'USA', '10001', 'Multi-sports facility'),
('Harmony Music Hall', '45', 'Broadway', 'Los Angeles', 'CA', 'USA', '90001', 'Music training center');