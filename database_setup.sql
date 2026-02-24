CREATE DATABASE IF NOT EXISTS tripx_db;
USE tripx_db;

-- Table: destinations
CREATE TABLE IF NOT EXISTS destinations (
    destination_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    type VARCHAR(50) NOT NULL,
    country VARCHAR(100) NOT NULL,
    city VARCHAR(100),
    best_season VARCHAR(50),
    description TEXT,
    timezone VARCHAR(50),
    average_rating DOUBLE DEFAULT 0.0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Table: activities (referenced in code)
CREATE TABLE IF NOT EXISTS activities (
    activity_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    destination_id BIGINT,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    price DOUBLE,
    capacity INT,
    category VARCHAR(50),
    FOREIGN KEY (destination_id) REFERENCES destinations(destination_id) ON DELETE CASCADE
);

-- Table: bookingdes
CREATE TABLE IF NOT EXISTS bookingdes (
    booking_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    booking_reference VARCHAR(50) NOT NULL UNIQUE,
    user_id INT NOT NULL,  -- In a real app, this would be a FK to users
    destination_id BIGINT,
    activity_id BIGINT,    -- Optional, null if booking just a trip
    start_at TIMESTAMP NOT NULL,
    end_at TIMESTAMP,
    num_guests INT DEFAULT 1,
    status VARCHAR(20) DEFAULT 'PENDING',  -- PENDING, CONFIRMED, CANCELLED, COMPLETED
    payment_status VARCHAR(20) DEFAULT 'UNPAID', -- UNPAID, PAID, REFUNDED
    total_amount DOUBLE DEFAULT 0.0,
    currency VARCHAR(10) DEFAULT 'USD',
    notes TEXT,
    stripe_payment_id VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (destination_id) REFERENCES destinations(destination_id) ON DELETE SET NULL,
    FOREIGN KEY (activity_id) REFERENCES activities(activity_id) ON DELETE SET NULL
);

-- If table already exists, add the stripe payment column:
ALTER TABLE bookingdes ADD COLUMN IF NOT EXISTS stripe_payment_id VARCHAR(255);

-- Add email column to bookings
ALTER TABLE bookingdes ADD COLUMN IF NOT EXISTS user_email VARCHAR(255);

-- Add average_rating column to activities
ALTER TABLE activities ADD COLUMN IF NOT EXISTS average_rating DOUBLE DEFAULT 0.0;

-- Table: reviews (for destinations and activities)
CREATE TABLE IF NOT EXISTS reviews (
    review_id    BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id      INT NOT NULL,
    target_type  VARCHAR(20) NOT NULL,  -- 'DESTINATION' or 'ACTIVITY'
    target_id    BIGINT NOT NULL,
    rating       INT NOT NULL,
    comment      TEXT,
    created_at   TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Insert some sample data
INSERT INTO destinations (name, type, country, city, best_season, description, timezone, average_rating) VALUES 
('Eiffel Tower', 'city', 'France', 'Paris', 'spring', 'The most famous landmark in Paris.', 'UTC+1', 4.8),
('Grand Canyon', 'mountain', 'USA', 'Arizona', 'autumn', 'A steep-sided canyon carved by the Colorado River.', 'UTC-7', 4.9),
('Maldives', 'island', 'Maldives', 'Male', 'winter', 'Tropical nation in the Indian Ocean.', 'UTC+5', 4.9);
