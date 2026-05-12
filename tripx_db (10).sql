-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Hôte : 127.0.0.1
-- Généré le : mar. 03 mars 2026 à 23:43
-- Version du serveur : 10.4.32-MariaDB
-- Version de PHP : 8.1.25

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Base de données : `tripx_db`
--

-- --------------------------------------------------------

--
-- Structure de la table `accommodation`
--

CREATE TABLE `accommodation` (
  `id` int(11) NOT NULL,
  `name` varchar(100) DEFAULT NULL,
  `type` varchar(50) DEFAULT NULL,
  `city` varchar(100) DEFAULT NULL,
  `country` varchar(100) DEFAULT NULL,
  `latitude` decimal(10,8) DEFAULT NULL,
  `longitude` decimal(11,8) DEFAULT NULL,
  `address` varchar(255) DEFAULT NULL,
  `postal_code` varchar(20) DEFAULT NULL,
  `description` text DEFAULT NULL,
  `stars` int(11) DEFAULT NULL,
  `rating` double DEFAULT NULL,
  `status` varchar(50) DEFAULT 'Active',
  `image_path` varchar(500) DEFAULT NULL,
  `phone` varchar(20) DEFAULT NULL,
  `email` varchar(100) DEFAULT NULL,
  `website` varchar(200) DEFAULT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp(),
  `updated_at` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  `accommodation_amenities` text DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Déchargement des données de la table `accommodation`
--

INSERT INTO `accommodation` (`id`, `name`, `type`, `city`, `country`, `latitude`, `longitude`, `address`, `postal_code`, `description`, `stars`, `rating`, `status`, `image_path`, `phone`, `email`, `website`, `created_at`, `updated_at`, `accommodation_amenities`) VALUES
(1, 'Hilton Tunis Updated', 'Hotel', 'Tunis', 'Tunisia', 36.80650000, 10.18150000, 'Lac 2', NULL, 'Updated description', 5, 4.8, 'Active', 'uploads/images/hotels/hilton-tunis.jpg', '+216 71 123 456', 'hiltontunisupdated@tripx.com', 'https://www.hilton-tunis-updated.com', '2026-02-08 08:21:47', '2026-02-08 22:24:15', NULL),
(3, 'Hilton Tunis', 'Hotel', 'Tunis', 'Tunisia', 36.80650000, 10.18150000, 'Les Berges du Lac', '63526', 'Luxury 5 star hotel with sea view', 5, 4.7, 'Active', '/uploads/images/hotels/73f48a85f3502c674800a93237d37ced_1771497028896.jpg', '217352742', 'hiltontunis@tripx.com', 'https://www.hilton-tunis.com', '2026-02-08 08:21:47', '2026-02-19 10:30:28', ''),
(4, 'Marina Bay Sands', 'Hotel', 'Tibesti تيبستي', 'Tchad تشاد', 21.94304600, 15.82031300, '10 Bayfront Ave', '838432', 'Iconic luxury hotel featuring the worlds largest infinity pool and a massive casino.', 5, 4.7, 'Active', '/uploads/images/hotels/6628f73b3140a804ab87e4ed3d16ae7c_1771497137263.jpg', '6834364376843', 'marinabaysands@tripx.com', 'https://www.marina-bay-sands.com', '2026-02-08 08:21:47', '2026-02-23 13:25:53', ''),
(5, 'The Ritz', 'Hotel', 'Paris', 'France', 48.85660000, 2.35220000, '15 Place Vendôme', '8588', 'Historic high-end stay known for its opulent decor and legendary service.', 5, 4.9, 'Active', '/uploads/images/hotels/863202c655d4ee34bb44a965d45c2c64_1771496909947.jpg', '732497364354', 'theritz@tripx.com', 'https://www.the-ritz.com', '2026-02-08 08:21:47', '2026-02-19 10:28:29', ''),
(8, 'Burj Al Arab', 'Hotel', 'Dubai', 'UAE', 25.07610000, 55.13270000, 'Jumeirah St', '7777', 'The worlds only \"7-star\" hotel, built on an artificial island with sail-shaped architecture.', 5, 4.9, 'Active', 'uploads/images/hotels/burj-al-arab.jpg', '86954536', 'burjalarab@tripx.com', 'https://www.burj-al-arab.com', '2026-02-08 08:21:47', '2026-02-23 13:23:04', ''),
(10, 'Belmond Hotel Das Cataratas', 'Hotel', 'تونس', 'تونس', 36.86625100, 10.31744200, 'Rodovia Gatatatas, km 28', '2070', 'The only hotel located inside the Iguazu National Park, right next to the falls.', 5, 4.9, 'Active', 'uploads/images/hotels/belmond-cataratas.jpg', '444446789', 'belmondhoteldascataratas@tripx.com', 'https://www.belmond-hotel-das-cataratas.com', '2026-02-08 08:21:47', '2026-02-23 13:09:28', ''),
(11, 'La Mamounia', 'Hotel', 'Marrakech', 'Morocco', 31.62080000, 7.99720000, 'Avenue Bab Jdid', '4467', 'Palatial hotel offering traditional Moroccan architecture and world-class gardens.', 5, 4.8, 'Active', 'uploads/images/hotels/la-mamounia.jpg', '777865443', 'lamamounia@tripx.com', 'https://www.la-mamounia.com', '2026-02-08 08:21:47', '2026-02-23 13:14:44', ''),
(12, 'Glass Igloo Resort', 'Hotel', 'Rovaniemi', 'Finland', 0.00000000, 0.00000000, 'Arctic Circle Road', '', 'Stay under the Northern Lights in a climate-controlled glass dome.', 4, 4.5, 'Active', '/uploads/images/hotels/glass_igloo_1770653400265.png', '+216 71 123 456', 'glassiglooresort@tripx.com', 'https://www.glass-igloo-resort.com', '2026-02-08 08:21:47', '2026-02-09 16:10:00', NULL),
(14, 'La cigale tabarka', 'Hotel', 'Tabarka', 'Turkey', 36.95505820, 8.79264050, 'tabarka', '3130', '5 stars hotel', 5, 0, 'Active', 'uploads/images/hotels/la_cigale_1770594864331.jpg', '+216 97766319', 'info@lacigaletabarka.com', 'lacigaletabarka.com', '2026-02-08 09:49:41', '2026-02-09 15:43:46', NULL),
(15, 'nada', 'Resort', 'kairouan', 'Tunisia', 40.36280000, 8.28531300, 'xsiyfisyqxfiysax', '3130', 'sxgiysxgc', 4, 0, 'Active', '/uploads/images/villas/61dc2ac33b3dd806be72a4448cd26728_1771496746971.jpg', '36437438569438', 'raghdselmi@gmail.com', 'esprit.tn', '2026-02-08 10:00:29', '2026-02-25 15:25:24', ''),
(17, 'epstein', 'Hotel', 'trump', 'USA', 56.65870000, 78.67950000, 'island', '12345', 'elon musk is not invited', 4, 0, 'Active', '/uploads/images/hotels/49fc4027380a311eb8687f35ac5aafcb_1771496692266.jpg', '35824249373', 'epstein@gmail.com', 'epstein.com', '2026-02-08 20:57:25', '2026-02-19 10:24:52', ''),
(18, 'epstein island', 'Hotel', 'trump', 'USA', 67.78800000, 55.80680000, 'ecgkedc', '1267', 'dxykedcvekc', 5, 0, 'Active', '/uploads/images/hotels/aa128b0769a6c12c77fd124ec97eddcc_1771491879531.jpg', '69059756454', 'raghd@esprit.tn', 'esprit.Tn', '2026-02-08 21:57:10', '2026-02-19 09:04:39', ''),
(19, 'dxhvjed', 'Villa', 'degced', 'Japan', 23.92230000, 26.28351000, 'zxdyfveducgxj', '7369', 'dcxfyedj vgejcn', 2, 0, 'Inactive', '/uploads/images/villas/32be02fec19e98d3c5eb7a8e09a1d1fa_1771496660293.jpg', '2731370132610', 'eipsten@gmail.com', 'eipstein.com', '2026-02-09 15:55:45', '2026-02-19 10:24:20', ''),
(20, 'trump', 'Resort', 'washington DC', 'USA', 63.79360000, 73.76300000, 'the white house', '3490', 'CIA', 1, 0, 'Active', '/uploads/images/hotels/be5dbaa118f4253e5444e8b52b424a32__1__1771491841256.jpg', '456788999', 'trump@gmail.com', 'trump.com', '2026-02-09 20:00:41', '2026-02-19 09:04:01', ''),
(21, 'gkecs', 'Guest House', 'sxyghsq', 'Italy', 87.86500000, 77.89700000, 'jhqxfsqjg', '7858', 'xjgsqx', 5, 0, 'Pending', '/uploads/images/hotels/5f88066c54306eef381184b490dbac79_1771491574157.jpg', '769956854', 'G@esprit.tn', 'www.esprit.tn', '2026-02-09 20:04:30', '2026-02-19 08:59:34', ''),
(22, 'DGKE', 'Villa', 'Эвенкийский район', 'Россия', 68.00757100, 105.46875000, 'JHZGS', '6859', 'luxury hotel in russia', 3, 0, 'Active', '/uploads/images/villas/2520fab111a14af1abf76471e0ee476d_1771491376842.jpg', '66778844', 'G@ESPRIT.TN', 'raghed.tn', '2026-02-09 20:15:03', '2026-02-19 10:43:27', 'Free Parking,Swimming Pool,24h Reception,24h Security,CCTV,Smoke Detectors'),
(29, 'Four seasons hotel Tunis', 'Hotel', 'تونس', 'تونس', 36.91943100, 10.29727900, 'gammarth beach road', '1120', 'luxury hotel in gammarth', 5, 0, 'Active', '/uploads/images/hotels/four_seasons_hotel_tunis_1771970187566.jpg', '97766319', 'fourseasons@gmail.com', 'www.fourseasonstunis.com', '2026-02-24 21:56:27', '2026-02-24 21:56:27', 'Wi-Fi,Air Conditioning,Heating,Elevator,Wheelchair Accessible,Pet Friendly,Smoking Allowed,Swimming Pool,Fitness Center,Spa & Wellness,Restaurant,Conference Rooms,Business Center,Laundry Service,24h Reception,Concierge,Room Service,Airport Shuttle,Breakfast Included,VIP Services,24h Security,In-room Safe,Fire Extinguishers,Smoke Detectors');

-- --------------------------------------------------------

--
-- Structure de la table `activities`
--

CREATE TABLE `activities` (
  `activity_id` bigint(20) UNSIGNED NOT NULL,
  `destination_id` bigint(20) UNSIGNED NOT NULL,
  `name` varchar(160) NOT NULL,
  `category` enum('tour','adventure','cultural','food','relaxation','nightlife','sports','wellness','other') NOT NULL,
  `description` text DEFAULT NULL,
  `duration_minutes` smallint(5) UNSIGNED DEFAULT NULL,
  `price` decimal(10,2) DEFAULT NULL,
  `currency` char(3) NOT NULL DEFAULT 'USD',
  `age_min` tinyint(3) UNSIGNED DEFAULT NULL,
  `capacity` smallint(5) UNSIGNED DEFAULT NULL,
  `available_from` date DEFAULT NULL,
  `available_to` date DEFAULT NULL,
  `meeting_point` varchar(255) DEFAULT NULL,
  `average_rating` decimal(3,2) NOT NULL DEFAULT 0.00,
  `is_active` tinyint(1) NOT NULL DEFAULT 1,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Déchargement des données de la table `activities`
--

INSERT INTO `activities` (`activity_id`, `destination_id`, `name`, `category`, `description`, `duration_minutes`, `price`, `currency`, `age_min`, `capacity`, `available_from`, `available_to`, `meeting_point`, `average_rating`, `is_active`, `created_at`) VALUES
(1, 115, 'quad', 'adventure', 'szdgjhqsolmjqgu', NULL, 30.00, 'USD', NULL, 10, NULL, NULL, NULL, 0.00, 1, '2026-02-21 21:11:24'),
(2, 97, 'Walk with elephants', 'adventure', 'walk with elephants', NULL, 40.00, 'USD', NULL, 7, NULL, NULL, NULL, 0.00, 1, '2026-02-23 11:22:46'),
(3, 117, 'Camel Trekking', 'adventure', 'Sunset safari through the dunes.', NULL, 80.00, 'USD', NULL, 10, NULL, NULL, NULL, 0.00, 1, '2026-02-23 11:51:08'),
(4, 119, 'Wine Tasting Tour', 'food', 'Visit historical vineyards and taste local wines.', NULL, 120.00, 'USD', NULL, 8, NULL, NULL, NULL, 0.00, 1, '2026-02-23 11:51:08'),
(5, 122, 'City Tour', 'adventure', 'Sunset safari through the dunes.', NULL, 55.00, 'USD', NULL, 10, NULL, NULL, NULL, 0.00, 1, '2026-02-23 13:51:28'),
(6, 117, 'Star Gazing', '', 'Night sky observation in the deep desert.', NULL, 40.00, 'USD', NULL, 20, NULL, NULL, NULL, 0.00, 1, '2026-02-23 13:51:28'),
(7, 118, 'Scuba Diving', 'adventure', 'Explore the vibrant coral reefs.', NULL, 150.00, 'USD', NULL, 6, NULL, NULL, NULL, 0.00, 1, '2026-02-23 13:51:28'),
(8, 118, 'Island Boat Tour', '', 'Visit secluded coves and lagoons.', NULL, 200.00, 'USD', NULL, 12, NULL, NULL, NULL, 0.00, 1, '2026-02-23 13:51:28'),
(9, 119, 'Wine Tasting Tour', 'food', 'Visit historical vineyards and taste local wines.', NULL, 120.00, 'USD', NULL, 8, NULL, NULL, NULL, 0.00, 1, '2026-02-23 13:51:28'),
(10, 119, 'Cooking Class', 'food', 'Learn to make authentic Italian pasta.', NULL, 95.00, 'USD', NULL, 10, NULL, NULL, NULL, 0.00, 1, '2026-02-23 13:51:28'),
(11, 5, 'Eating tapas', 'food', 'Discover historical landmarks and local secrets.', NULL, 30.00, 'USD', NULL, 15, NULL, NULL, NULL, 0.00, 1, '2026-02-23 13:51:28'),
(12, 29, 'City Walking Tour', '', 'Discover historical landmarks and local secrets.', NULL, 30.00, 'USD', NULL, 15, NULL, NULL, NULL, 0.00, 1, '2026-02-23 13:51:28'),
(13, 47, 'City Walking Tour', '', 'Discover historical landmarks and local secrets.', NULL, 30.00, 'USD', NULL, 15, NULL, NULL, NULL, 0.00, 1, '2026-02-23 13:51:28'),
(14, 111, 'Chasse', 'other', 'Discover historical landmarks and local secrets.', NULL, 30.00, 'USD', NULL, 15, NULL, NULL, NULL, 0.00, 1, '2026-02-23 13:51:28'),
(15, 54, 'Nothern lights', 'nightlife', 'Discover historical landmarks and local secrets.', NULL, 0.00, 'USD', NULL, 15, NULL, NULL, NULL, 0.00, 1, '2026-02-23 13:51:28'),
(16, 59, 'City Walking Tour', '', 'Discover historical landmarks and local secrets.', NULL, 30.00, 'USD', NULL, 15, NULL, NULL, NULL, 0.00, 1, '2026-02-23 13:51:28'),
(17, 64, 'City Walking Tour', '', 'Discover historical landmarks and local secrets.', NULL, 30.00, 'USD', NULL, 15, NULL, NULL, NULL, 0.00, 1, '2026-02-23 13:51:28'),
(19, 79, 'Monkey Zoo', 'adventure', 'Discover historical landmarks and local secrets.', NULL, 90.00, 'USD', NULL, 9, NULL, NULL, NULL, 0.00, 1, '2026-02-23 13:51:28'),
(21, 83, 'City Walking Tour', '', 'Discover historical landmarks and local secrets.', NULL, 30.00, 'USD', NULL, 15, NULL, NULL, NULL, 0.00, 1, '2026-02-23 13:51:28'),
(24, 116, 'City Walking Tour', '', 'Discover historical landmarks and local secrets.', NULL, 30.00, 'USD', NULL, 15, NULL, NULL, NULL, 0.00, 1, '2026-02-23 13:51:28'),
(25, 79, 'ef', 'food', 'ebt', NULL, 55.00, 'USD', NULL, 12, NULL, NULL, NULL, 0.00, 1, '2026-02-24 12:42:05'),
(26, 126, 'hiking', 'adventure', 'aze', NULL, 90.00, 'USD', NULL, 10, NULL, NULL, NULL, 0.00, 1, '2026-02-26 21:54:37'),
(27, 125, 'skii', 'adventure', 'kih', NULL, 50.00, 'USD', NULL, 7, NULL, NULL, NULL, 0.00, 1, '2026-02-26 21:54:57');

-- --------------------------------------------------------

--
-- Structure de la table `bookingacc`
--

CREATE TABLE `bookingacc` (
  `id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `room_id` int(11) NOT NULL,
  `check_in` date NOT NULL,
  `check_out` date NOT NULL,
  `total_price` decimal(10,2) NOT NULL,
  `number_of_guests` int(11) NOT NULL DEFAULT 1,
  `phone_number` varchar(20) DEFAULT NULL,
  `special_requests` text DEFAULT NULL,
  `estimated_arrival_time` varchar(20) DEFAULT NULL,
  `cancel_reason` text DEFAULT NULL,
  `rejection_reason` text DEFAULT NULL,
  `cancelled_at` datetime DEFAULT NULL,
  `rejected_at` datetime DEFAULT NULL,
  `status` enum('PENDING','CONFIRMED','CANCELLED','REJECTED') DEFAULT 'PENDING',
  `created_at` timestamp NOT NULL DEFAULT current_timestamp(),
  `google_calendar_event_id` varchar(255) DEFAULT NULL,
  `calendar_sync_status` enum('NOT_SYNCED','SYNCED','FAILED') NOT NULL DEFAULT 'NOT_SYNCED',
  `calendar_last_error` text DEFAULT NULL,
  `calendar_synced_at` datetime DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Déchargement des données de la table `bookingacc`
--

INSERT INTO `bookingacc` (`id`, `user_id`, `room_id`, `check_in`, `check_out`, `total_price`, `number_of_guests`, `phone_number`, `special_requests`, `estimated_arrival_time`, `cancel_reason`, `rejection_reason`, `cancelled_at`, `rejected_at`, `status`, `created_at`, `google_calendar_event_id`, `calendar_sync_status`, `calendar_last_error`, `calendar_synced_at`) VALUES
(3, 1, 15, '2026-02-21', '2026-02-23', 1800.00, 1, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'CANCELLED', '2026-02-19 22:04:25', NULL, 'NOT_SYNCED', NULL, NULL),
(4, 1, 15, '2026-02-24', '2026-02-26', 1800.00, 1, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'CANCELLED', '2026-02-19 22:30:26', NULL, 'NOT_SYNCED', NULL, NULL),
(5, 2, 16, '2026-02-23', '2026-02-25', 1328.00, 4, '+216 97 108 643', 'filled mini fridge', '15:00 - 18:00', NULL, NULL, NULL, '2026-02-20 22:43:29', 'REJECTED', '2026-02-20 21:12:44', NULL, 'NOT_SYNCED', NULL, NULL),
(11, 2, 17, '2026-02-26', '2026-02-28', 1400.00, 2, '+216 2637123', 'OKK', 'Not sure yet', NULL, NULL, NULL, NULL, 'CONFIRMED', '2026-02-23 11:11:10', NULL, 'NOT_SYNCED', NULL, NULL),
(12, 6, 18, '2026-03-03', '2026-03-05', 800.00, 5, '87463745', 'EJHDKC', 'Not sure yet', NULL, NULL, NULL, NULL, 'CONFIRMED', '2026-02-23 11:15:25', NULL, 'NOT_SYNCED', NULL, NULL),
(13, 6, 15, '2026-02-25', '2026-02-26', 900.00, 5, '+579458454', 'want a room service', '12:00 - 15:00', NULL, NULL, NULL, NULL, 'CONFIRMED', '2026-02-23 12:53:05', NULL, 'NOT_SYNCED', NULL, NULL),
(14, 6, 17, '2026-03-04', '2026-03-07', 2100.00, 3, '657485443', NULL, '15:00 - 18:00', NULL, NULL, NULL, NULL, 'CONFIRMED', '2026-02-23 12:54:05', NULL, 'NOT_SYNCED', NULL, NULL),
(15, 6, 18, '2026-02-25', '2026-03-03', 2400.00, 5, '54263654', NULL, '12:00 - 15:00', NULL, NULL, NULL, NULL, 'CONFIRMED', '2026-02-23 12:55:03', NULL, 'NOT_SYNCED', NULL, NULL),
(16, 6, 19, '2026-02-28', '2026-03-03', 1500.00, 2, '875476778', NULL, 'Not sure yet', NULL, NULL, NULL, NULL, 'CONFIRMED', '2026-02-23 12:56:08', NULL, 'NOT_SYNCED', NULL, NULL),
(17, 6, 21, '2026-02-26', '2026-02-28', 1554.00, 2, '86545745', NULL, '06:00 - 09:00', NULL, NULL, NULL, NULL, 'CONFIRMED', '2026-02-23 13:31:28', NULL, 'NOT_SYNCED', NULL, NULL),
(18, 6, 20, '2026-05-13', '2026-05-17', 2600.00, 1, '86574754', NULL, '15:00 - 18:00', NULL, NULL, NULL, NULL, 'CONFIRMED', '2026-02-23 13:32:25', NULL, 'NOT_SYNCED', NULL, NULL),
(19, 6, 8, '2026-04-08', '2026-04-24', 1040.00, 1, '776968685', NULL, '12:00 - 15:00', NULL, NULL, NULL, NULL, 'CONFIRMED', '2026-02-23 13:34:42', NULL, 'NOT_SYNCED', NULL, NULL),
(20, 6, 7, '2026-06-17', '2026-06-19', 90.00, 1, '9757856856', NULL, '12:00 - 15:00', NULL, NULL, NULL, NULL, 'CONFIRMED', '2026-02-23 13:35:15', NULL, 'NOT_SYNCED', NULL, NULL),
(21, 6, 23, '2026-03-25', '2026-03-27', 1930.00, 2, '684784754', NULL, '18:00 - 21:00', NULL, NULL, NULL, NULL, 'CONFIRMED', '2026-02-23 13:35:45', NULL, 'NOT_SYNCED', NULL, NULL),
(22, 6, 7, '2026-03-07', '2026-03-08', 45.00, 1, '76485457', NULL, '21:00 - 00:00', NULL, NULL, NULL, NULL, 'CONFIRMED', '2026-02-23 13:40:16', NULL, 'NOT_SYNCED', NULL, NULL),
(23, 6, 22, '2026-02-27', '2026-03-03', 6000.00, 2, '5658343648', NULL, '06:00 - 09:00', NULL, NULL, NULL, NULL, 'CONFIRMED', '2026-02-23 13:40:56', NULL, 'NOT_SYNCED', NULL, NULL),
(24, 6, 9, '2026-04-15', '2026-04-17', 24000.00, 2, '645323323', NULL, '15:00 - 18:00', NULL, NULL, NULL, NULL, 'CONFIRMED', '2026-02-23 13:41:41', NULL, 'NOT_SYNCED', NULL, NULL),
(25, 6, 9, '2026-07-23', '2026-07-31', 96000.00, 2, '768463477', NULL, '06:00 - 09:00', NULL, NULL, NULL, NULL, 'CONFIRMED', '2026-02-23 13:42:14', NULL, 'NOT_SYNCED', NULL, NULL),
(26, 6, 22, '2026-10-06', '2026-10-11', 7500.00, 2, '753653542', NULL, 'Not sure yet', NULL, NULL, NULL, NULL, 'CONFIRMED', '2026-02-23 13:42:58', NULL, 'NOT_SYNCED', NULL, NULL),
(27, 5, 18, '2026-04-05', '2026-04-09', 1600.00, 2, '324545665', 'okk', '09:00 - 12:00', NULL, NULL, NULL, NULL, 'CONFIRMED', '2026-02-23 22:58:23', NULL, 'NOT_SYNCED', NULL, NULL),
(28, 4, 25, '2026-05-12', '2026-05-16', 5880.00, 2, '534323288', NULL, '12:00 - 15:00', NULL, NULL, NULL, NULL, 'CONFIRMED', '2026-02-23 23:12:58', '0sdvop5ovlae0upl0jgs9lubm8', 'SYNCED', NULL, '2026-02-24 00:21:58'),
(29, 4, 8, '2026-03-27', '2026-03-29', 130.00, 1, '4546787798', NULL, '18:00 - 21:00', NULL, NULL, NULL, NULL, 'CONFIRMED', '2026-02-23 23:25:08', '446slfn80q4nd0okgpsf22rr6g', 'SYNCED', NULL, '2026-02-24 00:25:51'),
(30, 6, 30, '2026-03-27', '2026-03-29', 3000.00, 2, '99888766', 'HGDCHS', '15:00 - 18:00', NULL, NULL, NULL, NULL, 'CONFIRMED', '2026-02-26 12:52:32', 'vmtrs40fsadodah60ngmbd1cvs', 'SYNCED', NULL, '2026-03-01 13:00:26'),
(31, 18, 30, '2026-03-05', '2026-03-06', 1500.00, 2, '68565343', '9607565', 'Not sure yet', NULL, NULL, NULL, NULL, 'PENDING', '2026-02-27 03:06:08', NULL, 'NOT_SYNCED', NULL, NULL),
(32, 26, 30, '2026-03-08', '2026-03-10', 3000.00, 2, '77788990', 'uxsgvdgxjvdxd', '21:00 - 00:00', NULL, NULL, NULL, NULL, 'CONFIRMED', '2026-02-27 05:12:46', 'hsb4rm61nnr7bnhmtj3pc8nv2o', 'SYNCED', NULL, '2026-03-01 13:00:06');

-- --------------------------------------------------------

--
-- Structure de la table `bookingdes`
--

CREATE TABLE `bookingdes` (
  `booking_id` bigint(20) UNSIGNED NOT NULL,
  `booking_reference` varchar(36) NOT NULL,
  `user_id` bigint(20) UNSIGNED DEFAULT NULL,
  `destination_id` bigint(20) UNSIGNED DEFAULT NULL,
  `activity_id` bigint(20) UNSIGNED DEFAULT NULL,
  `start_at` datetime NOT NULL,
  `end_at` datetime DEFAULT NULL,
  `num_guests` smallint(5) UNSIGNED NOT NULL DEFAULT 1,
  `status` enum('pending','confirmed','cancelled','completed') NOT NULL DEFAULT 'pending',
  `payment_status` enum('unpaid','paid','refunded','partial') NOT NULL DEFAULT 'unpaid',
  `total_amount` decimal(10,2) NOT NULL DEFAULT 0.00,
  `currency` char(3) NOT NULL DEFAULT 'USD',
  `notes` text DEFAULT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp(),
  `user_email` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Déchargement des données de la table `bookingdes`
--

INSERT INTO `bookingdes` (`booking_id`, `booking_reference`, `user_id`, `destination_id`, `activity_id`, `start_at`, `end_at`, `num_guests`, `status`, `payment_status`, `total_amount`, `currency`, `notes`, `created_at`, `user_email`) VALUES
(1, 'BK-0CF12526', 1, 97, NULL, '2026-06-17 00:00:00', '2026-07-01 23:59:59', 2, 'pending', 'unpaid', 0.00, 'USD', 'azefgdtyjgh', '2026-02-21 21:14:08', NULL),
(2, 'BK-80756E10', 1, 119, 4, '2026-05-07 00:00:00', '2026-05-15 23:59:59', 3, 'pending', 'unpaid', 360.00, 'USD', 'azertyuiokjnbvcxszertgvfdftyuioplkjhgvcxsedfghjnbvcxsdert', '2026-02-23 12:18:05', NULL),
(4, 'BK-5F8A7173', 1, 119, 4, '2026-02-26 00:00:00', '2026-03-07 23:59:59', 1, 'confirmed', 'unpaid', 120.00, 'USD', '', '2026-02-23 13:33:40', NULL),
(5, 'BK-3D66ABA9', 1, 73, NULL, '2026-06-10 00:00:00', '2026-06-18 23:59:59', 11, 'confirmed', 'unpaid', 0.00, 'USD', '', '2026-02-23 13:34:55', NULL),
(6, 'BK-14C88A1F', 1, 117, 6, '2026-03-18 00:00:00', '2026-03-28 23:59:59', 4, 'pending', 'unpaid', 160.00, 'USD', '', '2026-02-23 14:03:09', NULL),
(7, 'BK-11F9CDED', 1, 111, 14, '2026-02-25 00:00:00', '2026-02-26 23:59:59', 1, 'cancelled', 'unpaid', 30.00, 'USD', '', '2026-02-24 13:11:50', NULL),
(8, 'BK-43AD92B2', 1, 121, NULL, '2026-03-17 00:00:00', '2026-03-24 23:59:59', 2, 'pending', 'unpaid', 0.00, 'USD', '', '2026-02-24 15:24:01', NULL),
(9, 'BK-B0FFBC49', 22, 116, 24, '2026-02-25 00:00:00', '2026-02-28 23:59:59', 1, 'pending', 'unpaid', 30.00, 'USD', '', '2026-02-24 21:04:44', NULL),
(10, 'BK-18236CB6', 9, 124, NULL, '2026-02-28 00:00:00', '2026-03-03 23:59:59', 2, 'pending', 'unpaid', 0.00, 'USD', 'hi', '2026-02-26 21:44:46', NULL),
(11, 'BK-94E36432', 9, 111, 14, '2026-02-28 00:00:00', '2026-03-01 23:59:59', 2, 'confirmed', 'unpaid', 60.00, 'USD', '+-+', '2026-02-26 21:47:34', NULL),
(12, 'BK-BB14555E', 6, 126, 26, '2026-02-28 00:00:00', '2026-03-03 23:59:59', 2, 'pending', 'unpaid', 180.00, 'USD', '', '2026-02-26 22:19:00', NULL),
(13, 'BK-56930734', 6, 64, 17, '2026-02-28 00:00:00', '2026-02-28 23:59:59', 1, 'confirmed', 'unpaid', 30.00, 'USD', 'ef', '2026-02-26 22:19:46', NULL),
(14, 'BK-5819CA11', 6, 125, 27, '2026-03-12 00:00:00', '2026-03-13 23:59:59', 1, 'cancelled', 'unpaid', 50.00, 'USD', '', '2026-02-26 22:22:51', NULL),
(15, 'BK-333726ED', 6, 127, NULL, '2026-03-06 00:00:00', '2026-03-13 23:59:59', 3, 'confirmed', 'unpaid', 0.00, 'USD', 'brabi', '2026-02-26 22:54:41', NULL),
(16, 'BK-F65FCF47', 6, 126, 26, '2026-05-06 00:00:00', '2026-05-31 23:59:59', 2, 'confirmed', 'unpaid', 180.00, 'USD', 'hmdddddddddddddddd', '2026-02-26 22:55:52', NULL),
(17, 'BK-C00C3FBF', 6, 59, 16, '2026-03-07 00:00:00', '2026-03-14 23:59:59', 5, 'confirmed', 'unpaid', 150.00, 'USD', '', '2026-02-26 22:57:17', NULL),
(18, 'BK-9F465256', 1, 125, 27, '2026-03-04 00:00:00', '2026-03-07 23:59:59', 4, 'pending', 'unpaid', 200.00, 'USD', 'ouuuu', '2026-02-27 03:14:34', NULL),
(19, 'BK-90AF5F9D', 26, 126, 26, '2026-03-11 00:00:00', '2026-03-21 23:59:59', 3, 'confirmed', 'unpaid', 270.00, 'USD', '', '2026-03-01 12:06:22', NULL);

-- --------------------------------------------------------

--
-- Structure de la table `bookingtrans`
--

CREATE TABLE `bookingtrans` (
  `booking_id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `transport_id` int(11) NOT NULL,
  `schedule_id` int(11) DEFAULT NULL,
  `booking_date` datetime DEFAULT current_timestamp(),
  `adults_count` int(11) DEFAULT 0,
  `children_count` int(11) DEFAULT 0,
  `total_seats` int(11) NOT NULL,
  `total_price` decimal(10,2) NOT NULL,
  `booking_status` enum('PENDING','CONFIRMED','CANCELLED') DEFAULT 'PENDING',
  `payment_status` enum('UNPAID','PAID','REFUNDED') DEFAULT 'UNPAID',
  `insurance_included` tinyint(1) DEFAULT 0,
  `qr_code` varchar(255) DEFAULT NULL,
  `voucher_path` varchar(255) DEFAULT NULL,
  `ai_price_prediction` double DEFAULT NULL,
  `comparison_score` double DEFAULT NULL,
  `cancellation_reason` text DEFAULT NULL,
  `pickup_latitude` double DEFAULT NULL,
  `pickup_longitude` double DEFAULT NULL,
  `pickup_address` varchar(255) DEFAULT NULL,
  `dropoff_latitude` double DEFAULT NULL,
  `dropoff_longitude` double DEFAULT NULL,
  `dropoff_address` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Déchargement des données de la table `bookingtrans`
--

INSERT INTO `bookingtrans` (`booking_id`, `user_id`, `transport_id`, `schedule_id`, `booking_date`, `adults_count`, `children_count`, `total_seats`, `total_price`, `booking_status`, `payment_status`, `insurance_included`, `qr_code`, `voucher_path`, `ai_price_prediction`, `comparison_score`, `cancellation_reason`, `pickup_latitude`, `pickup_longitude`, `pickup_address`, `dropoff_latitude`, `dropoff_longitude`, `dropoff_address`) VALUES
(32, 1, 26, NULL, '2026-02-25 17:46:33', 1, 0, 1, 200.00, 'CONFIRMED', 'PAID', 0, NULL, NULL, 0, 0, '', NULL, NULL, NULL, NULL, NULL, NULL),
(40, 1, 26, 33, '2026-02-25 17:55:32', 8, 3, 11, 2475.00, 'CONFIRMED', 'PAID', 1, NULL, NULL, 0, 0, '', NULL, NULL, NULL, NULL, NULL, NULL),
(41, 1, 26, 33, '2026-02-25 18:03:06', 1, 0, 1, 200.00, 'CONFIRMED', 'PAID', 0, NULL, NULL, 0, 0, 'Cancelled by user', NULL, NULL, NULL, NULL, NULL, NULL),
(42, 1, 26, 33, '2026-02-25 18:04:36', 1, 0, 1, 200.00, 'CONFIRMED', 'PAID', 0, NULL, NULL, 0, 0, 'Cancelled by user', NULL, NULL, NULL, NULL, NULL, NULL),
(43, 1, 26, 33, '2026-02-25 18:04:46', 1, 0, 1, 200.00, 'CANCELLED', 'UNPAID', 0, NULL, NULL, 0, 0, 'Cancelled by user', NULL, NULL, NULL, NULL, NULL, NULL),
(58, 1, 26, NULL, '2026-02-25 21:44:45', 3, 1, 4, 1760.00, 'CANCELLED', 'UNPAID', 0, NULL, NULL, 0, 0, '', NULL, NULL, NULL, NULL, NULL, NULL),
(63, 1, 26, 33, '2026-02-25 22:04:48', 1, 0, 1, 200.00, 'CANCELLED', 'UNPAID', 0, NULL, NULL, 0, 0, 'Cancelled by user', NULL, NULL, NULL, NULL, NULL, NULL),
(69, 1, 26, NULL, '2026-02-25 22:31:16', 1, 0, 1, 200.00, 'CANCELLED', 'UNPAID', 0, NULL, NULL, 0, 0, '', NULL, NULL, NULL, NULL, NULL, NULL),
(72, 1, 26, NULL, '2026-02-25 23:07:11', 1, 0, 1, 200.00, 'CANCELLED', 'UNPAID', 0, NULL, NULL, 0, 0, '', NULL, NULL, NULL, NULL, NULL, NULL),
(81, 1, 26, 33, '2026-02-26 01:05:18', 1, 0, 1, 200.00, 'PENDING', 'UNPAID', 0, NULL, NULL, 0, 0, NULL, NULL, NULL, NULL, NULL, NULL, NULL),
(82, 1, 26, NULL, '2026-02-26 01:06:04', 1, 0, 1, 200.00, 'PENDING', 'UNPAID', 0, NULL, NULL, 0, 0, NULL, NULL, NULL, NULL, NULL, NULL, NULL),
(88, 1, 39, NULL, '2026-02-26 08:22:24', 3, 0, 3, 66.00, 'PENDING', 'UNPAID', 0, NULL, NULL, 0, 0, NULL, NULL, NULL, NULL, NULL, NULL, NULL),
(89, 1, 30, 44, '2026-02-26 09:23:55', 3, 0, 3, 6.00, 'CONFIRMED', 'PAID', 0, NULL, NULL, 0, 0, NULL, NULL, NULL, NULL, NULL, NULL, NULL),
(90, 1, 37, NULL, '2026-02-26 11:39:14', 3, 0, 3, 108.00, 'PENDING', 'UNPAID', 0, NULL, NULL, 0, 0, NULL, NULL, NULL, NULL, NULL, NULL, NULL),
(91, 1, 39, NULL, '2026-02-26 11:44:29', 2, 0, 2, 20.00, 'PENDING', 'UNPAID', 0, NULL, NULL, 0, 0, NULL, NULL, NULL, NULL, NULL, NULL, NULL),
(92, 1, 26, NULL, '2026-02-26 11:45:19', 1, 0, 1, 20.00, 'PENDING', 'UNPAID', 0, NULL, NULL, 0, 0, NULL, NULL, NULL, NULL, NULL, NULL, NULL),
(94, 1, 30, 44, '2026-02-26 11:45:51', 4, 0, 4, 8.00, 'PENDING', 'UNPAID', 0, NULL, NULL, 0, 0, NULL, NULL, NULL, NULL, NULL, NULL, NULL),
(95, 1, 30, 44, '2026-02-26 12:19:36', 1, 0, 1, 2.00, 'PENDING', 'UNPAID', 0, NULL, NULL, 0, 0, NULL, NULL, NULL, NULL, NULL, NULL, NULL),
(96, 1, 26, 53, '2026-02-26 13:04:53', 1, 0, 1, 20.00, 'PENDING', 'UNPAID', 0, NULL, NULL, 0, 0, NULL, NULL, NULL, NULL, NULL, NULL, NULL),
(97, 1, 26, NULL, '2026-02-26 13:23:27', 1, 0, 1, 20.00, 'PENDING', 'UNPAID', 0, NULL, NULL, 0, 0, NULL, NULL, NULL, NULL, NULL, NULL, NULL),
(98, 1, 30, 44, '2026-02-26 13:25:54', 4, 0, 4, 8.00, 'PENDING', 'UNPAID', 0, NULL, NULL, 0, 0, NULL, NULL, NULL, NULL, NULL, NULL, NULL),
(99, 1, 44, NULL, '2026-02-26 13:48:37', 3, 0, 3, 24000000.00, 'PENDING', 'UNPAID', 0, NULL, NULL, 0, 0, NULL, NULL, NULL, NULL, NULL, NULL, NULL),
(100, 1, 44, NULL, '2026-02-26 13:49:06', 6, 0, 6, 48000000.00, 'PENDING', 'UNPAID', 0, NULL, NULL, 0, 0, NULL, NULL, NULL, NULL, NULL, NULL, NULL),
(101, 1, 44, 54, '2026-02-26 13:51:07', 2, 0, 2, 16000000.00, 'PENDING', 'UNPAID', 0, NULL, NULL, 0, 0, NULL, NULL, NULL, NULL, NULL, NULL, NULL),
(102, 1, 26, 55, '2026-02-27 19:16:41', 4, 4, 8, 352.00, 'PENDING', 'UNPAID', 0, NULL, NULL, 0, 0, NULL, 36.85297750491024, 10.234794616699219, 'مطار تونس قرطاج الدولي, شارع الزعيم ياسر عرفات, المنقطة الصناعية الشرقية 1, الشرقية, معتمدية حي الخضراء, تونس, ولاية تونس, 2035, تونس', 36.768317229575864, 10.274963378906252, 'شارع الجمهورية, رادس المدينة, رادس, معتمدية رادس, ولاية بن عروس, 2040, تونس'),
(103, 26, 30, 44, '2026-03-01 13:02:20', 3, 3, 6, 26.40, 'CONFIRMED', 'PAID', 0, NULL, NULL, 0, 0, NULL, NULL, NULL, NULL, NULL, NULL, NULL);

-- --------------------------------------------------------

--
-- Structure de la table `comments`
--

CREATE TABLE `comments` (
  `id` int(11) NOT NULL,
  `post_id` int(11) DEFAULT NULL,
  `travel_story_id` int(11) DEFAULT NULL,
  `user_id` int(11) NOT NULL,
  `parent_comment_id` int(11) DEFAULT NULL,
  `body` text NOT NULL,
  `created_at` datetime DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Déchargement des données de la table `comments`
--

INSERT INTO `comments` (`id`, `post_id`, `travel_story_id`, `user_id`, `parent_comment_id`, `body`, `created_at`) VALUES
(2, 1, NULL, 1, NULL, 'bbhvghvcgcvh', '2026-02-19 01:26:21'),
(3, 1, NULL, 1, NULL, 'vh g', '2026-02-19 01:41:37'),
(5, NULL, 3, 1, NULL, 'hubh', '2026-02-19 01:46:04'),
(6, NULL, 3, 1, NULL, 'bvg', '2026-02-19 01:46:56'),
(7, NULL, 2, 1, NULL, 'bvhg', '2026-02-19 01:46:59'),
(8, 6, NULL, 1, NULL, 'ergerghnt', '2026-02-19 05:48:16'),
(12, 5, NULL, 1, NULL, 'efzef', '2026-02-19 08:05:22'),
(14, NULL, 6, 1, NULL, 'efzef', '2026-02-19 08:32:53'),
(15, 6, NULL, 1, NULL, 'efzef', '2026-02-19 08:33:09'),
(16, 6, NULL, 1, NULL, 'srgdr', '2026-02-19 09:33:26'),
(17, NULL, 6, 1, NULL, 'rgeq', '2026-02-19 09:33:39'),
(22, 7, NULL, 3, NULL, 'dfbwdg', '2026-03-01 00:38:34'),
(23, 11, NULL, 4, NULL, 'e\'tgeq\'t', '2026-03-01 04:50:36'),
(24, NULL, 9, 4, NULL, 'ydhjsrzh(h', '2026-03-01 04:52:08'),
(25, 3, NULL, 4, NULL, 'rgqerg', '2026-03-01 05:40:08'),
(26, 11, NULL, 4, NULL, 'dger', '2026-03-01 05:59:47'),
(28, 11, NULL, 4, 26, 'tgsreger', '2026-03-01 06:04:12'),
(29, NULL, 8, 4, NULL, 'rgfqge', '2026-03-01 06:16:26'),
(30, 12, NULL, 4, NULL, 'zez', '2026-03-01 06:21:56'),
(32, NULL, 9, 4, 24, 'argg', '2026-03-01 06:31:48'),
(33, 12, NULL, 4, NULL, 'dd', '2026-03-02 05:25:19'),
(34, 14, NULL, 4, NULL, 'zefze', '2026-03-02 06:13:23'),
(35, 14, NULL, 4, NULL, 'rffre', '2026-03-02 10:07:43'),
(36, 16, NULL, 4, NULL, 'efzef', '2026-03-03 05:52:38'),
(37, 17, NULL, 4, NULL, 'zefzEF', '2026-03-03 06:30:09'),
(38, NULL, 23, 4, NULL, 'efzef', '2026-03-03 16:10:22'),
(39, NULL, 23, 4, 38, 'srf<sf', '2026-03-03 16:10:52'),
(40, 19, NULL, 26, NULL, 'woww', '2026-03-03 21:47:08');

-- --------------------------------------------------------

--
-- Structure de la table `community_statistics`
--

CREATE TABLE `community_statistics` (
  `id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `posts_count` int(11) DEFAULT 0,
  `comments_count` int(11) DEFAULT 0,
  `reactions_count` int(11) DEFAULT 0,
  `badges_count` int(11) DEFAULT 0,
  `followers_count` int(11) DEFAULT 0,
  `other_stats` varchar(1000) DEFAULT NULL,
  `updated_at` datetime DEFAULT current_timestamp() ON UPDATE current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Structure de la table `destinations`
--

CREATE TABLE `destinations` (
  `destination_id` bigint(20) UNSIGNED NOT NULL,
  `name` varchar(150) NOT NULL,
  `type` enum('city','beach','mountain','countryside','desert','island','forest','cruise','other') NOT NULL,
  `country` varchar(100) NOT NULL,
  `city` varchar(100) DEFAULT NULL,
  `best_season` enum('spring','summer','autumn','winter','all_year') NOT NULL,
  `description` text DEFAULT NULL,
  `timezone` varchar(64) DEFAULT NULL,
  `average_rating` decimal(3,2) NOT NULL DEFAULT 0.00,
  `image_url` varchar(500) DEFAULT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp(),
  `latitude` decimal(10,8) DEFAULT NULL,
  `longitude` decimal(11,8) DEFAULT NULL,
  `estimated_budget` decimal(10,2) DEFAULT NULL,
  `popularity` int(11) DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Déchargement des données de la table `destinations`
--

INSERT INTO `destinations` (`destination_id`, `name`, `type`, `country`, `city`, `best_season`, `description`, `timezone`, `average_rating`, `image_url`, `created_at`, `latitude`, `longitude`, `estimated_budget`, `popularity`) VALUES
(3, 'Swiss Alps', 'mountain', 'Switzerland', 'Zermatt', 'winter', 'Perfect for skiing and mountain adventures', 'Europe/Zurich', 4.50, 'https://picsum.photos/seed/travel/250/150', '2026-02-20 12:24:30', NULL, NULL, NULL, 0),
(5, 'Barcelona City', 'city', 'Spain', 'Barcelona', 'summer', 'Vibrant city with Mediterranean beaches and Gaudi architecture', 'Europe/Madrid', 4.50, 'https://picsum.photos/seed/travel/250/150', '2026-02-20 12:24:30', NULL, NULL, NULL, 0),
(6, 'Iceland Aurora', 'other', 'Iceland', 'Reykjavik', 'winter', 'Northern lights and volcanic landscapes', 'Atlantic/Reykjavik', 4.50, 'https://picsum.photos/seed/travel/250/150', '2026-02-20 12:24:30', NULL, NULL, NULL, 0),
(7, 'Bali Paradise', 'beach', 'Indonesia', 'Bali', 'summer', 'Tropical paradise with beautiful beaches and lush jungles', 'Asia/Makassar', 4.50, 'https://picsum.photos/seed/travel/250/150', '2026-02-20 12:32:41', NULL, NULL, NULL, 0),
(8, 'Greek Islands', 'island', 'Greece', 'Santorini', 'summer', 'Mediterranean beauty with white houses and blue sea', 'Europe/Athens', 4.50, 'https://picsum.photos/seed/travel/250/150', '2026-02-20 12:32:41', NULL, NULL, NULL, 0),
(9, 'Morocco', 'desert', 'Morocco', 'casablanca', 'summer', 'Perfect for skiing and mountain adventures', 'africa', 4.50, 'https://picsum.photos/seed/travel/250/150', '2026-02-20 12:32:41', NULL, NULL, NULL, 0),
(10, 'Maldives Resort', 'island', 'Maldives', 'Male', 'all_year', 'Luxury overwater bungalows in tropical paradise', 'Indian/Maldives', 4.50, 'https://picsum.photos/seed/travel/250/150', '2026-02-20 12:32:41', NULL, NULL, NULL, 0),
(16, 'Maldives Resort', 'island', 'Maldives', 'Male', 'all_year', 'Luxury overwater bungalows in tropical paradise', 'Indian/Maldives', 4.50, 'https://picsum.photos/seed/travel/250/150', '2026-02-21 10:17:36', NULL, NULL, NULL, 0),
(18, 'Iceland Aurora', 'other', 'Iceland', 'Reykjavik', 'winter', 'Northern lights and volcanic landscapes', 'Atlantic/Reykjavik', 4.50, 'https://picsum.photos/seed/travel/250/150', '2026-02-21 10:17:36', NULL, NULL, NULL, 0),
(20, 'Greek Islands', 'island', 'Greece', 'Santorini', 'summer', 'Mediterranean beauty with white houses and blue sea', 'Europe/Athens', 4.50, 'https://picsum.photos/seed/travel/250/150', '2026-02-21 10:17:43', NULL, NULL, NULL, 0),
(25, 'Bali Paradise', 'beach', 'Indonesia', 'Bali', 'summer', 'Tropical paradise with beautiful beaches and lush jungles', 'Asia/Makassar', 4.50, 'https://picsum.photos/seed/travel/250/150', '2026-02-21 10:21:29', NULL, NULL, NULL, 0),
(26, 'Greek Islands', 'island', 'Greece', 'Santorini', 'summer', 'Mediterranean beauty with white houses and blue sea', 'Europe/Athens', 4.50, 'https://picsum.photos/seed/travel/250/150', '2026-02-21 10:21:29', NULL, NULL, NULL, 0),
(27, 'Swiss Alps', 'mountain', 'Switzerland', 'Zermatt', 'winter', 'Perfect for skiing and mountain adventures', 'Europe/Zurich', 4.50, 'https://picsum.photos/seed/travel/250/150', '2026-02-21 10:21:29', NULL, NULL, NULL, 0),
(28, 'Maldives Resort', 'island', 'Maldives', 'Male', 'all_year', 'Luxury overwater bungalows in tropical paradise', 'Indian/Maldives', 4.50, 'https://picsum.photos/seed/travel/250/150', '2026-02-21 10:21:29', NULL, NULL, NULL, 0),
(29, 'Barcelona City', 'city', 'Spain', 'Barcelona', 'summer', 'Vibrant city with Mediterranean beaches and Gaudi architecture', 'Europe/Madrid', 4.50, 'https://picsum.photos/seed/travel/250/150', '2026-02-21 10:21:29', NULL, NULL, NULL, 0),
(30, 'Iceland Aurora', 'other', 'Iceland', 'Reykjavik', 'winter', 'Northern lights and volcanic landscapes', 'Atlantic/Reykjavik', 4.50, 'https://picsum.photos/seed/travel/250/150', '2026-02-21 10:21:29', NULL, NULL, NULL, 0),
(32, 'Holland', 'countryside', 'Holland', 'gcf', 'summer', 'Mediterranean beauty with white houses and blue sea', 'Europe/Athens', 4.50, 'https://picsum.photos/seed/travel/250/150', '2026-02-21 10:24:07', NULL, NULL, NULL, 0),
(34, 'Maldives Resort', 'island', 'Maldives', 'Male', 'all_year', 'Luxury overwater bungalows in tropical paradise', 'Indian/Maldives', 4.50, 'https://picsum.photos/seed/travel/250/150', '2026-02-21 10:24:07', NULL, NULL, NULL, 0),
(36, 'Iceland Aurora', 'other', 'Iceland', 'Reykjavik', 'winter', 'Northern lights and volcanic landscapes', 'Atlantic/Reykjavik', 4.50, 'https://picsum.photos/seed/travel/250/150', '2026-02-21 10:24:07', NULL, NULL, NULL, 0),
(38, 'Greek Islands', 'island', 'Greece', 'Santorini', 'summer', 'Mediterranean beauty with white houses and blue sea', 'Europe/Athens', 4.50, 'https://picsum.photos/seed/travel/250/150', '2026-02-21 10:29:10', NULL, NULL, NULL, 0),
(39, 'Swiss Alps', 'mountain', 'Switzerland', 'Zermatt', 'winter', 'Perfect for skiing and mountain adventures', 'Europe/Zurich', 4.50, 'https://picsum.photos/seed/travel/250/150', '2026-02-21 10:29:10', NULL, NULL, NULL, 0),
(40, 'Maldives Resort', 'island', 'Maldives', 'Male', 'all_year', 'Luxury overwater bungalows in tropical paradise', 'Indian/Maldives', 4.50, 'https://picsum.photos/seed/travel/250/150', '2026-02-21 10:29:10', NULL, NULL, NULL, 0),
(42, 'Iceland Aurora', 'other', 'Iceland', 'Reykjavik', 'winter', 'Northern lights and volcanic landscapes', 'Atlantic/Reykjavik', 4.50, 'https://picsum.photos/seed/travel/250/150', '2026-02-21 10:29:10', NULL, NULL, NULL, 0),
(43, 'Sweden', 'island', 'Sweden', 'Stockholm', 'autumn', 'Trokbqcs;h, xv', 'Scandinavia', 2.00, 'https://picsum.photos/seed/travel/250/150', '2026-02-21 10:30:15', NULL, NULL, NULL, 0),
(44, 'Greek Islands', 'island', 'Greece', 'Santorini', 'summer', 'Mediterranean beauty with white houses and blue sea', 'Europe/Athens', 4.50, 'https://picsum.photos/seed/travel/250/150', '2026-02-21 10:30:15', NULL, NULL, NULL, 0),
(45, 'Swiss Alps', 'mountain', 'Switzerland', 'Zermatt', 'winter', 'Perfect for skiing and mountain adventures', 'Europe/Zurich', 4.50, 'https://picsum.photos/seed/travel/250/150', '2026-02-21 10:30:15', NULL, NULL, NULL, 0),
(46, 'Maldives Resort', 'island', 'Maldives', 'Male', 'all_year', 'Luxury overwater bungalows in tropical paradise', 'Indian/Maldives', 4.50, 'https://picsum.photos/seed/travel/250/150', '2026-02-21 10:30:15', NULL, NULL, NULL, 0),
(47, 'Barcelona City', 'city', 'Spain', 'Barcelona', 'summer', 'Vibrant city with Mediterranean beaches and Gaudi architecture', 'Europe/Madrid', 4.50, 'https://picsum.photos/seed/travel/250/150', '2026-02-21 10:30:15', NULL, NULL, NULL, 0),
(48, 'Iceland Aurora', 'other', 'Iceland', 'Reykjavik', 'winter', 'Northern lights and volcanic landscapes', 'Atlantic/Reykjavik', 4.50, 'https://picsum.photos/seed/travel/250/150', '2026-02-21 10:30:15', NULL, NULL, NULL, 0),
(49, 'Bali Paradise', 'beach', 'Indonesia', 'Bali', 'summer', 'Tropical paradise with beautiful beaches and lush jungles', 'Asia/Makassar', 4.50, 'https://picsum.photos/seed/travel/250/150', '2026-02-21 10:34:28', NULL, NULL, NULL, 0),
(50, 'Greek Islands', 'island', 'Greece', 'Santorini', 'summer', 'Mediterranean beauty with white houses and blue sea', 'Europe/Athens', 4.50, 'https://picsum.photos/seed/travel/250/150', '2026-02-21 10:34:28', NULL, NULL, NULL, 0),
(51, 'Swiss Alps', 'mountain', 'Switzerland', 'Zermatt', 'winter', 'Perfect for skiing and mountain adventures', 'Europe/Zurich', 4.50, 'https://picsum.photos/seed/travel/250/150', '2026-02-21 10:34:28', NULL, NULL, NULL, 0),
(52, 'Maldives Resort', 'island', 'Maldives', 'Male', 'all_year', 'Luxury overwater bungalows in tropical paradise', 'Indian/Maldives', 4.50, 'https://picsum.photos/seed/travel/250/150', '2026-02-21 10:34:28', NULL, NULL, NULL, 0),
(53, 'Barcelona City', 'city', 'Spain', 'Barcelona', 'summer', 'Vibrant city with Mediterranean beaches and Gaudi architecture', 'Europe/Madrid', 4.50, 'https://picsum.photos/seed/travel/250/150', '2026-02-21 10:34:28', NULL, NULL, NULL, 0),
(54, 'danemark', 'city', 'eddinburgh', 'Reykjavik', 'winter', 'Northern lights and volcanic landscapes', 'Atlantic/Reykjavik', 4.50, 'https://picsum.photos/seed/travel/250/150', '2026-02-21 10:34:28', NULL, NULL, NULL, 0),
(55, 'Bali Paradise', 'beach', 'Indonesia', 'Bali', 'summer', 'Tropical paradise with beautiful beaches and lush jungles', 'Asia/Makassar', 4.50, 'https://picsum.photos/seed/travel/250/150', '2026-02-21 10:34:55', NULL, NULL, NULL, 0),
(56, 'Greek Islands', 'island', 'Greece', 'Santorini', 'summer', 'Mediterranean beauty with white houses and blue sea', 'Europe/Athens', 4.50, 'https://picsum.photos/seed/travel/250/150', '2026-02-21 10:34:55', NULL, NULL, NULL, 0),
(57, 'Swiss Alps', 'mountain', 'Switzerland', 'Zermatt', 'winter', 'Perfect for skiing and mountain adventures', 'Europe/Zurich', 4.50, 'https://picsum.photos/seed/travel/250/150', '2026-02-21 10:34:55', NULL, NULL, NULL, 0),
(58, 'Maldives Resort', 'island', 'Maldives', 'Male', 'all_year', 'Luxury overwater bungalows in tropical paradise', 'Indian/Maldives', 4.50, 'https://picsum.photos/seed/travel/250/150', '2026-02-21 10:34:55', NULL, NULL, NULL, 0),
(59, 'Barcelona City', 'city', 'Spain', 'Barcelona', 'summer', 'Vibrant city with Mediterranean beaches and Gaudi architecture', 'Europe/Madrid', 4.50, 'https://picsum.photos/seed/travel/250/150', '2026-02-21 10:34:55', NULL, NULL, NULL, 1),
(60, 'Norway', 'other', 'Norway', 'azerty', 'winter', 'Northern lights and volcanic landscapes', 'Atlantic', 3.00, 'https://picsum.photos/seed/travel/250/150', '2026-02-21 10:34:55', NULL, NULL, NULL, 0),
(61, 'Bali Paradise', 'beach', 'Indonesia', 'Bali', 'summer', 'Tropical paradise with beautiful beaches and lush jungles', 'Asia/Makassar', 4.50, 'https://picsum.photos/seed/travel/250/150', '2026-02-21 10:37:51', NULL, NULL, NULL, 0),
(62, 'Greek Islands', 'island', 'Greece', 'Santorini', 'summer', 'Mediterranean beauty with white houses and blue sea', 'Europe/Athens', 4.50, 'https://picsum.photos/seed/travel/250/150', '2026-02-21 10:37:51', NULL, NULL, NULL, 0),
(63, 'Swiss Alps', 'mountain', 'Switzerland', 'Zermatt', 'winter', 'Perfect for skiing and mountain adventures', 'Europe/Zurich', 4.50, 'https://picsum.photos/seed/travel/250/150', '2026-02-21 10:37:51', NULL, NULL, NULL, 0),
(64, 'Tunisia', 'city', 'Tunis', 'Tunis', 'all_year', 'Luxury overwater bungalows in tropical paradise', 'africa', 4.00, 'https://picsum.photos/seed/travel/250/150', '2026-02-21 10:37:51', NULL, NULL, NULL, 1),
(65, 'Barcelona City', 'city', 'Spain', 'Barcelona', 'summer', 'Vibrant city with Mediterranean beaches and Gaudi architecture', 'Europe/Madrid', 4.50, 'https://picsum.photos/seed/travel/250/150', '2026-02-21 10:37:51', NULL, NULL, NULL, 0),
(66, 'Iceland Aurora', 'other', 'Iceland', 'Reykjavik', 'winter', 'Northern lights and volcanic landscapes', 'Atlantic/Reykjavik', 4.50, 'https://picsum.photos/seed/travel/250/150', '2026-02-21 10:37:51', NULL, NULL, NULL, 0),
(67, 'Bali Paradise', 'beach', 'Indonesia', 'Bali', 'summer', 'Tropical paradise with beautiful beaches and lush jungles', 'Asia/Makassar', 4.50, 'https://picsum.photos/seed/travel/250/150', '2026-02-21 10:38:07', NULL, NULL, NULL, 0),
(68, 'Greek Islands', 'island', 'Greece', 'Santorini', 'summer', 'Mediterranean beauty with white houses and blue sea', 'Europe/Athens', 4.50, 'https://picsum.photos/seed/travel/250/150', '2026-02-21 10:38:07', NULL, NULL, NULL, 0),
(69, 'Swiss Alps', 'mountain', 'Switzerland', 'Zermatt', 'winter', 'Perfect for skiing and mountain adventures', 'Europe/Zurich', 4.50, 'https://picsum.photos/seed/travel/250/150', '2026-02-21 10:38:07', NULL, NULL, NULL, 0),
(70, 'Maldives Resort', 'island', 'Maldives', 'Male', 'all_year', 'Luxury overwater bungalows in tropical paradise', 'Indian/Maldives', 4.50, 'https://picsum.photos/seed/travel/250/150', '2026-02-21 10:38:07', NULL, NULL, NULL, 0),
(71, 'Barcelona City', 'city', 'Spain', 'Barcelona', 'summer', 'Vibrant city with Mediterranean beaches and Gaudi architecture', 'Europe/Madrid', 4.50, 'https://picsum.photos/seed/travel/250/150', '2026-02-21 10:38:07', NULL, NULL, NULL, 0),
(72, 'Iceland Aurora', 'other', 'Iceland', 'Reykjavik', 'winter', 'Northern lights and volcanic landscapes', 'Atlantic/Reykjavik', 4.50, 'https://picsum.photos/seed/travel/250/150', '2026-02-21 10:38:07', NULL, NULL, NULL, 0),
(73, 'Mali', 'beach', 'africa', 'Bali', 'summer', 'Tropical paradise with beautiful beaches and lush jungles', 'africa', 4.50, 'https://picsum.photos/seed/travel/250/150', '2026-02-21 13:06:37', NULL, NULL, NULL, 1),
(74, 'Greek Islands', 'island', 'Greece', 'Santorini', 'summer', 'Mediterranean beauty with white houses and blue sea', 'Europe/Athens', 4.50, 'https://picsum.photos/seed/travel/250/150', '2026-02-21 13:06:37', NULL, NULL, NULL, 0),
(75, 'Swiss Alps', 'mountain', 'Switzerland', 'Zermatt', 'winter', 'Perfect for skiing and mountain adventures', 'Europe/Zurich', 4.50, 'https://picsum.photos/seed/travel/250/150', '2026-02-21 13:06:37', NULL, NULL, NULL, 0),
(76, 'Maldives Resort', 'island', 'Maldives', 'Male', 'all_year', 'Luxury overwater bungalows in tropical paradise', 'Indian/Maldives', 4.50, 'https://picsum.photos/seed/travel/250/150', '2026-02-21 13:06:37', NULL, NULL, NULL, 0),
(77, 'Barcelona City', 'city', 'Spain', 'Barcelona', 'summer', 'Vibrant city with Mediterranean beaches and Gaudi architecture', 'Europe/Madrid', 4.50, 'https://picsum.photos/seed/travel/250/150', '2026-02-21 13:06:37', NULL, NULL, NULL, 0),
(78, 'Iceland Aurora', 'other', 'Iceland', 'Reykjavik', 'winter', 'Northern lights and volcanic landscapes', 'Atlantic/Reykjavik', 4.50, 'https://picsum.photos/seed/travel/250/150', '2026-02-21 13:06:37', NULL, NULL, NULL, 0),
(79, 'Bali Paradise', 'beach', 'Indonesia', 'Bali', 'summer', 'Tropical paradise with beautiful beaches and lush jungles', 'Asia/Makassar', 4.50, 'https://picsum.photos/seed/travel/250/150', '2026-02-21 13:10:57', NULL, NULL, NULL, 0),
(80, 'Greek Islands', 'island', 'Greece', 'Santorini', 'summer', 'Mediterranean beauty with white houses and blue sea', 'Europe/Athens', 4.50, 'https://picsum.photos/seed/travel/250/150', '2026-02-21 13:10:57', NULL, NULL, NULL, 0),
(81, 'Swiss Alps', 'mountain', 'Switzerland', 'Zermatt', 'winter', 'Perfect for skiing and mountain adventures', 'Europe/Zurich', 4.50, 'https://picsum.photos/seed/travel/250/150', '2026-02-21 13:10:57', NULL, NULL, NULL, 0),
(82, 'Maldives Resort', 'island', 'Maldives', 'Male', 'all_year', 'Luxury overwater bungalows in tropical paradise', 'Indian/Maldives', 4.50, 'https://picsum.photos/seed/travel/250/150', '2026-02-21 13:10:57', NULL, NULL, NULL, 0),
(83, 'Barcelona City', 'city', 'Spain', 'Barcelona', 'summer', 'Vibrant city with Mediterranean beaches and Gaudi architecture', 'Europe/Madrid', 4.50, 'https://picsum.photos/seed/travel/250/150', '2026-02-21 13:10:57', NULL, NULL, NULL, 0),
(84, 'Madagascar', 'other', 'Africa', 'Reykjavik', 'spring', 'Northern lights and volcanic landscapes', 'Africa', 4.50, 'https://picsum.photos/seed/travel/250/150', '2026-02-21 13:10:57', NULL, NULL, NULL, 0),
(85, 'Thailand', 'cruise', 'Indonesia', 'Bali', 'summer', 'Tropical paradise with beautiful beaches and lush jungles', 'Asia/Makassar', 4.50, 'https://picsum.photos/seed/travel/250/150', '2026-02-21 13:13:44', NULL, NULL, NULL, 0),
(86, 'Greek Islands', 'island', 'Greece', 'Santorini', 'summer', 'Mediterranean beauty with white houses and blue sea', 'Europe/Athens', 4.50, 'https://picsum.photos/seed/travel/250/150', '2026-02-21 13:13:44', NULL, NULL, NULL, 0),
(87, 'Swiss Alps', 'mountain', 'Switzerland', 'Zermatt', 'winter', 'Perfect for skiing and mountain adventures', 'Europe/Zurich', 4.50, 'https://picsum.photos/seed/travel/250/150', '2026-02-21 13:13:44', NULL, NULL, NULL, 0),
(88, 'Ohio', 'countryside', 'USA', 'Ohio', 'all_year', 'Luxury overwater bungalows in tropical paradise', 'Usa', 3.00, 'https://picsum.photos/seed/travel/250/150', '2026-02-21 13:13:44', NULL, NULL, NULL, 0),
(90, 'Iceland Aurora', 'other', 'Iceland', 'Reykjavik', 'winter', 'Northern lights and volcanic landscapes', 'Atlantic/Reykjavik', 4.50, 'https://picsum.photos/seed/travel/250/150', '2026-02-21 13:13:44', NULL, NULL, NULL, 0),
(91, 'Bali Paradise', 'beach', 'Indonesia', 'Bali', 'summer', 'Tropical paradise with beautiful beaches and lush jungles', 'Asia/Makassar', 4.50, 'https://picsum.photos/seed/travel/250/150', '2026-02-21 13:16:30', NULL, NULL, NULL, 0),
(92, 'Greek Islands', 'island', 'Greece', 'Santorini', 'summer', 'Mediterranean beauty with white houses and blue sea', 'Europe/Athens', 4.50, 'https://picsum.photos/seed/travel/250/150', '2026-02-21 13:16:30', NULL, NULL, NULL, 0),
(93, 'Swiss Alps', 'mountain', 'Switzerland', 'Zermatt', 'winter', 'Perfect for skiing and mountain adventures', 'Europe/Zurich', 4.50, 'https://picsum.photos/seed/travel/250/150', '2026-02-21 13:16:30', NULL, NULL, NULL, 0),
(94, 'Maldives Resort', 'island', 'Maldives', 'Male', 'all_year', 'Luxury overwater bungalows in tropical paradise', 'Indian/Maldives', 4.50, 'https://picsum.photos/seed/travel/250/150', '2026-02-21 13:16:30', NULL, NULL, NULL, 0),
(95, 'Barcelona City', 'city', 'Spain', 'Barcelona', 'summer', 'Vibrant city with Mediterranean beaches and Gaudi architecture', 'Europe/Madrid', 4.50, 'https://picsum.photos/seed/travel/250/150', '2026-02-21 13:16:30', NULL, NULL, NULL, 0),
(96, 'Iceland Aurora', 'other', 'Iceland', 'Reykjavik', 'winter', 'Northern lights and volcanic landscapes', 'Atlantic/Reykjavik', 4.50, 'https://picsum.photos/seed/travel/250/150', '2026-02-21 13:16:30', NULL, NULL, NULL, 0),
(97, 'Bali Paradise', 'beach', 'Indonesia', 'Bali', 'summer', 'Tropical paradise with beautiful beaches and lush jungles', 'Asia/Makassar', 4.50, 'https://picsum.photos/seed/travel/250/150', '2026-02-21 13:28:23', NULL, NULL, NULL, 1),
(98, 'Greek Islands', 'island', 'Greece', 'Santorini', 'summer', 'Mediterranean beauty with white houses and blue sea', 'Europe/Athens', 4.50, 'https://picsum.photos/seed/travel/250/150', '2026-02-21 13:28:23', NULL, NULL, NULL, 0),
(99, 'Swiss Alps', 'mountain', 'Switzerland', 'Zermatt', 'winter', 'Perfect for skiing and mountain adventures', 'Europe/Zurich', 4.50, 'https://picsum.photos/seed/travel/250/150', '2026-02-21 13:28:23', NULL, NULL, NULL, 0),
(100, 'Maldives Resort', 'island', 'Maldives', 'Male', 'all_year', 'Luxury overwater bungalows in tropical paradise', 'Indian/Maldives', 4.50, 'https://picsum.photos/seed/travel/250/150', '2026-02-21 13:28:23', NULL, NULL, NULL, 0),
(110, 'Greek Islands', 'island', 'Greece', 'Santorini', 'summer', 'Mediterranean beauty with white houses and blue sea', 'Europe/Athens', 4.50, 'https://picsum.photos/seed/travel/250/150', '2026-02-21 13:35:55', NULL, NULL, NULL, 0),
(111, 'Canada', 'forest', 'USA', 'Quebec', 'winter', 'Perfect for skiing and mountain adventures', 'america', 4.50, 'https://picsum.photos/seed/travel/250/150', '2026-02-21 13:35:55', NULL, NULL, NULL, 2),
(112, 'Maldives Resort', 'island', 'Maldives', 'Male', 'all_year', 'Luxury overwater bungalows in tropical paradise', 'Indian/Maldives', 4.50, 'https://picsum.photos/seed/travel/250/150', '2026-02-21 13:35:55', NULL, NULL, NULL, 0),
(113, 'Barcelona City', 'city', 'Spain', 'Barcelona', 'summer', 'Vibrant city with Mediterranean beaches and Gaudi architecture', 'Europe/Madrid', 4.50, 'https://picsum.photos/seed/travel/250/150', '2026-02-21 13:35:55', NULL, NULL, NULL, 0),
(114, 'Iceland Aurora', 'other', 'Iceland', 'Reykjavik', 'winter', 'Northern lights and volcanic landscapes', 'Atlantic/Reykjavik', 4.50, 'https://picsum.photos/seed/travel/250/150', '2026-02-21 13:35:55', NULL, NULL, NULL, 0),
(115, 'Tunisia', 'beach', 'hammamet', 'tunis', 'summer', 'zssz', '12', 3.00, 'https://picsum.photos/seed/travel/250/150', '2026-02-21 14:24:32', NULL, NULL, NULL, 0),
(116, 'new york', 'city', 'USA', 'new york', 'autumn', 'azertyuiopmlkjnbvcde', '80', 5.00, 'https://picsum.photos/seed/travel/250/150', '2026-02-21 21:13:02', NULL, NULL, NULL, 1),
(117, 'Sahara Desert', 'desert', 'Algeria', 'Tamanrasset', 'winter', 'Experience the vast golden dunes and star-filled nights.', 'GMT+1', 4.80, 'https://images.unsplash.com/photo-1509316785289-025f5b846b35?q=80&w=250&h=150&auto=format&fit=crop', '2026-02-23 11:51:08', NULL, NULL, NULL, 1),
(118, 'Seychelles Beach', 'beach', 'Seychelles', 'Mahe', 'spring', 'Pristine white sands and crystal clear turquoise waters.', 'GMT+4', 4.90, 'https://images.unsplash.com/photo-1589979485637-f98c301510af?q=80&w=250&h=150&auto=format&fit=crop', '2026-02-23 11:51:08', NULL, NULL, NULL, 0),
(119, 'Tuscan Countryside', 'countryside', 'Italy', 'Siena', 'autumn', 'Rolling hills, vineyards, and medieval villages.', 'GMT+1', 4.70, 'https://images.unsplash.com/photo-1534447677768-be436bb09401?q=80&w=250&h=150&auto=format&fit=crop', '2026-02-23 11:51:08', NULL, NULL, NULL, 2),
(120, 'Sahara Desert', 'desert', 'Algeria', 'Tamanrasset', 'winter', 'Experience the vast golden dunes and star-filled nights.', 'GMT+1', 4.80, 'https://images.unsplash.com/photo-1509316785289-025f5b846b35?q=80&w=250&h=150&auto=format&fit=crop', '2026-02-23 13:51:28', NULL, NULL, NULL, 0),
(121, 'Seychelles Beach', 'beach', 'Seychelles', 'Mahe', 'spring', 'Pristine white sands and crystal clear turquoise waters.', 'GMT+4', 4.90, 'https://images.unsplash.com/photo-1589979485637-f98c301510af?q=80&w=250&h=150&auto=format&fit=crop', '2026-02-23 13:51:28', NULL, NULL, NULL, 1),
(122, 'Tuscan Countryside', 'countryside', 'Italy', 'Siena', 'autumn', 'Rolling hills, vineyards, and medieval villages.', 'GMT+1', 4.70, 'https://images.unsplash.com/photo-1534447677768-be436bb09401?q=80&w=250&h=150&auto=format&fit=crop', '2026-02-23 13:51:28', NULL, NULL, NULL, 0),
(123, 'Rome', 'city', 'Italy', 'Rome', 'summer', 'AZERTYUOIKJBVCFXD', 'UTC+01:00', 5.00, NULL, '2026-02-26 20:28:37', NULL, NULL, 0.00, 0),
(124, 'Washington', 'city', 'USA', 'Washington', 'spring', 'm', 'UTC-12:00', 1.00, NULL, '2026-02-26 21:20:56', 36.80000000, 10.16000000, 2000.00, 1),
(125, 'Madrid', 'city', 'Spain', 'madrid', 'summer', 'Beatiful', 'UTC+2', 5.00, NULL, '2026-02-26 21:52:03', 40.41670000, 3.70330000, 600.00, 2),
(126, 'Paris', 'city', 'France', 'Paris', 'summer', 'love love love', 'UTC-10:00', 4.50, NULL, '2026-02-26 21:53:00', 48.85750000, 2.35140000, 5000.00, 2),
(127, 'Berlin', 'city', 'Germany', 'Berlin', 'autumn', 'zvf', 'UTC+01:00', 3.00, NULL, '2026-02-26 22:30:38', 12.00000000, 90.00000000, 700.00, 1),
(128, 'Washington', 'city', 'USA', 'Washington', 'spring', 'm', 'UTC-12:00', 1.00, NULL, '2026-03-03 15:39:46', 36.80000000, 10.16000000, 2000.00, 0),
(129, 'Washington', 'city', 'USA', 'Washington', 'spring', 'm', 'UTC-12:00', 1.00, NULL, '2026-03-03 15:39:56', 36.80000000, 10.16000000, 2000.00, 0);

-- --------------------------------------------------------

--
-- Structure de la table `destination_trans`
--

CREATE TABLE `destination_trans` (
  `destination_id` int(11) NOT NULL,
  `name` varchar(150) NOT NULL,
  `type` enum('city','beach','mountain','countryside','desert','island','forest','cruise','other') NOT NULL,
  `country` varchar(100) NOT NULL,
  `city` varchar(100) DEFAULT NULL,
  `best_season` enum('spring','summer','autumn','winter','all_year') NOT NULL,
  `description` text DEFAULT NULL,
  `timezone` varchar(64) DEFAULT NULL,
  `average_rating` decimal(3,2) NOT NULL DEFAULT 0.00,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Déchargement des données de la table `destination_trans`
--

INSERT INTO `destination_trans` (`destination_id`, `name`, `type`, `country`, `city`, `best_season`, `description`, `timezone`, `average_rating`, `created_at`) VALUES
(1, 'TestCityA', 'city', 'France', 'Paris', 'summer', 'Test destination A', 'Europe/Paris', 4.50, '2026-02-18 21:14:02'),
(2, 'TestCityB', 'city', 'UK', 'London', 'spring', 'Test destination B', 'Europe/London', 4.20, '2026-02-18 21:14:03');

-- --------------------------------------------------------

--
-- Structure de la table `followings`
--

CREATE TABLE `followings` (
  `id` int(11) NOT NULL,
  `follower_id` int(11) NOT NULL,
  `followed_id` int(11) NOT NULL,
  `created_at` datetime DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Structure de la table `loyalty_points`
--

CREATE TABLE `loyalty_points` (
  `id` int(11) NOT NULL,
  `user_id` int(11) DEFAULT NULL,
  `total_points` int(11) DEFAULT 0,
  `level` enum('BRONZE','SILVER','GOLD') DEFAULT 'BRONZE',
  `created_at` timestamp NOT NULL DEFAULT current_timestamp(),
  `updated_at` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Déchargement des données de la table `loyalty_points`
--

INSERT INTO `loyalty_points` (`id`, `user_id`, `total_points`, `level`, `created_at`, `updated_at`) VALUES
(4, 26, 60, 'BRONZE', '2026-03-01 00:20:22', '2026-03-01 11:31:42');

-- --------------------------------------------------------

--
-- Structure de la table `offers`
--

CREATE TABLE `offers` (
  `id_offer` int(11) NOT NULL,
  `title` varchar(150) NOT NULL,
  `description` text DEFAULT NULL,
  `discount_type` enum('PERCENTAGE','FIXED') NOT NULL,
  `discount_value` decimal(5,2) NOT NULL,
  `pack_id` int(11) DEFAULT NULL,
  `destination_id` bigint(20) UNSIGNED DEFAULT NULL,
  `accommodation_id` int(11) DEFAULT NULL,
  `start_date` date DEFAULT NULL,
  `end_date` date DEFAULT NULL,
  `is_active` tinyint(1) DEFAULT 1
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Déchargement des données de la table `offers`
--

INSERT INTO `offers` (`id_offer`, `title`, `description`, `discount_type`, `discount_value`, `pack_id`, `destination_id`, `accommodation_id`, `start_date`, `end_date`, `is_active`) VALUES
(1, 'Summer Deal', '10% off on Spain Adventure', 'PERCENTAGE', 10.00, 1, NULL, NULL, '2026-01-01', '2026-12-31', 1),
(2, 'spring deal', 'enjoy the beauty of spring in tabarka', 'PERCENTAGE', 10.50, 3, NULL, NULL, '2026-03-01', '2026-03-29', 1);

-- --------------------------------------------------------

--
-- Structure de la table `packs`
--

CREATE TABLE `packs` (
  `id_pack` int(11) NOT NULL,
  `title` varchar(150) NOT NULL,
  `description` text DEFAULT NULL,
  `destination_id` bigint(20) UNSIGNED DEFAULT NULL,
  `accommodation_id` int(11) DEFAULT NULL,
  `activity_id` bigint(20) UNSIGNED DEFAULT NULL,
  `transport_id` int(11) DEFAULT NULL,
  `category_id` int(11) DEFAULT NULL,
  `duration_days` int(11) DEFAULT NULL,
  `base_price` decimal(10,2) DEFAULT NULL,
  `status` enum('ACTIVE','INACTIVE') DEFAULT 'ACTIVE',
  `created_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Déchargement des données de la table `packs`
--

INSERT INTO `packs` (`id_pack`, `title`, `description`, `destination_id`, `accommodation_id`, `activity_id`, `transport_id`, `category_id`, `duration_days`, `base_price`, `status`, `created_at`) VALUES
(1, 'Spain Adventure', 'A fun trip to Spain', 5, 17, 11, 28, 1, 7, 599.99, 'ACTIVE', '2026-02-28 12:03:39'),
(2, 'Life Refresh', 'its like restarting your life ;)', 3, 14, 1, 42, 3, 6, 700.00, 'ACTIVE', '2026-02-28 12:03:39'),
(3, 'Marriage Package', 'Romantic getaway', 29, 8, 12, 34, 2, 5, 5000.00, 'ACTIVE', '2026-02-28 12:03:39');

-- --------------------------------------------------------

--
-- Structure de la table `packs_bookings`
--

CREATE TABLE `packs_bookings` (
  `id_booking` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `pack_id` int(11) NOT NULL,
  `booking_date` timestamp NOT NULL DEFAULT current_timestamp(),
  `travel_start_date` date NOT NULL,
  `travel_end_date` date NOT NULL,
  `num_travelers` int(11) DEFAULT 1,
  `total_price` decimal(10,2) NOT NULL COMMENT 'Original pack price',
  `discount_applied` decimal(5,2) DEFAULT 0.00 COMMENT 'Total discount percentage (offer + loyalty)',
  `final_price` decimal(10,2) NOT NULL COMMENT 'Price after all discounts',
  `status` enum('PENDING','CONFIRMED','CANCELLED','COMPLETED') DEFAULT 'PENDING',
  `notes` text DEFAULT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp(),
  `updated_at` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Déchargement des données de la table `packs_bookings`
--

INSERT INTO `packs_bookings` (`id_booking`, `user_id`, `pack_id`, `booking_date`, `travel_start_date`, `travel_end_date`, `num_travelers`, `total_price`, `discount_applied`, `final_price`, `status`, `notes`, `created_at`, `updated_at`) VALUES
(3, 26, 2, '2026-03-01 11:31:42', '2026-04-05', '2026-04-08', 3, 2100.00, 4.00, 2016.00, 'CONFIRMED', '', '2026-03-01 11:31:42', '2026-03-01 11:57:47');

-- --------------------------------------------------------

--
-- Structure de la table `pack_categories`
--

CREATE TABLE `pack_categories` (
  `id_category` int(11) NOT NULL,
  `name` varchar(100) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Déchargement des données de la table `pack_categories`
--

INSERT INTO `pack_categories` (`id_category`, `name`) VALUES
(1, 'Family'),
(2, 'Couple'),
(3, 'Adventure'),
(4, 'Luxury'),
(5, 'raghed');

-- --------------------------------------------------------

--
-- Structure de la table `posts`
--

CREATE TABLE `posts` (
  `id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `trip_id` int(11) DEFAULT NULL,
  `title` varchar(255) NOT NULL,
  `body` text NOT NULL,
  `type` enum('inquiry','story','review','advice','other') NOT NULL,
  `image_url` varchar(500) DEFAULT NULL,
  `created_at` datetime DEFAULT current_timestamp(),
  `updated_at` datetime DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  `is_confirmed` tinyint(1) DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Déchargement des données de la table `posts`
--

INSERT INTO `posts` (`id`, `user_id`, `trip_id`, `title`, `body`, `type`, `image_url`, `created_at`, `updated_at`, `is_confirmed`) VALUES
(1, 1, 0, 'hhvhghv', 'gvghv', 'other', NULL, '2026-02-19 01:22:26', '2026-02-19 01:22:26', 0),
(3, 1, NULL, 'hb', 'uhnnnnnnnnnnn', 'other', 'https://www.dreamstime.com/photos-images/immage.html', '2026-02-19 02:12:53', '2026-02-19 02:12:53', 0),
(4, 1, NULL, 'hjbhu', 'bj j', 'other', NULL, '2026-02-19 03:43:59', '2026-02-19 03:43:59', 0),
(5, 1, NULL, 'bvh', 'hbbgkgh', 'other', NULL, '2026-02-19 05:06:18', '2026-02-19 05:06:18', 0),
(6, 1, NULL, 'nou1', 'tryingkjnlkj', 'other', NULL, '2026-02-19 05:47:53', '2026-02-19 08:58:50', 0),
(7, 1, NULL, 'Inquery', 'What do you suggest for my trip to spain?', 'other', NULL, '2026-02-19 09:34:07', '2026-02-19 13:35:50', 1),
(11, 4, NULL, '-uy(-u', 'esrgser(u,kfyuk', 'other', NULL, '2026-03-01 04:50:29', '2026-03-01 04:51:24', 0),
(12, 4, NULL, 'ew', 'tired', 'other', NULL, '2026-03-01 06:21:49', '2026-03-01 06:21:49', 0),
(13, 4, NULL, 'new1', 'fkku', 'other', NULL, '2026-03-02 05:25:54', '2026-03-02 05:25:54', 0),
(14, 4, NULL, 'Bali', 'cool', 'other', 'C:\\Users\\nmour\\OneDrive\\Pictures\\image_0.jpg', '2026-03-02 05:59:33', '2026-03-02 05:59:33', 0),
(16, 4, NULL, 'qf', 'qefef', 'other', 'C:\\Users\\nmour\\OneDrive\\Pictures\\Screenshots\\Screenshot 2026-02-14 033300.png', '2026-03-03 05:50:48', '2026-03-03 05:50:48', 0),
(17, 4, NULL, 'EZFZ', 'zefzef', 'other', 'C:\\Users\\nmour\\Downloads\\dcc5544b-c8dd-4c3b-a2b4-b0f91b240544.jpg', '2026-03-03 06:30:02', '2026-03-03 06:30:02', 0),
(18, 4, NULL, 'zefz', 'zeFZ<F', 'other', NULL, '2026-03-03 17:32:22', '2026-03-03 17:32:22', 0),
(19, 4, NULL, 'zefZFE', 'zefZEF', 'other', 'C:\\Users\\nmour\\OneDrive\\Pictures\\image_0.jpg', '2026-03-03 17:44:31', '2026-03-03 17:45:09', 0);

-- --------------------------------------------------------

--
-- Structure de la table `reactions`
--

CREATE TABLE `reactions` (
  `id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `post_id` int(11) DEFAULT NULL,
  `travel_story_id` int(11) DEFAULT NULL,
  `comment_id` int(11) DEFAULT NULL,
  `type` enum('like','love','haha','wow','sad','angry','other') NOT NULL,
  `created_at` datetime DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Déchargement des données de la table `reactions`
--

INSERT INTO `reactions` (`id`, `user_id`, `post_id`, `travel_story_id`, `comment_id`, `type`, `created_at`) VALUES
(1, 1, 1, NULL, NULL, 'like', '2026-03-01 04:57:27'),
(2, 4, 1, NULL, NULL, 'haha', '2026-03-01 05:39:59'),
(4, 4, 3, NULL, NULL, 'wow', '2026-03-01 05:40:17'),
(5, 4, NULL, 3, NULL, 'haha', '2026-03-01 05:40:26'),
(7, 4, NULL, NULL, 26, 'haha', '2026-03-01 06:04:24'),
(8, 4, NULL, NULL, 22, 'wow', '2026-03-01 06:11:45'),
(9, 4, NULL, NULL, 14, 'angry', '2026-03-01 06:11:55'),
(11, 4, NULL, 6, NULL, 'love', '2026-03-01 06:15:24'),
(16, 4, NULL, 8, NULL, 'wow', '2026-03-01 06:16:21'),
(17, 4, NULL, NULL, 29, 'haha', '2026-03-01 06:16:28'),
(21, 4, NULL, NULL, 28, 'sad', '2026-03-01 06:20:45'),
(30, 4, 11, NULL, NULL, 'love', '2026-03-01 06:50:03'),
(31, 4, NULL, NULL, 24, 'sad', '2026-03-01 06:50:54'),
(32, 4, 12, NULL, NULL, 'haha', '2026-03-02 03:15:55'),
(33, 4, 6, NULL, NULL, 'haha', '2026-03-02 03:25:39'),
(34, 4, 5, NULL, NULL, 'wow', '2026-03-02 03:25:41'),
(35, 4, NULL, NULL, 30, 'wow', '2026-03-02 05:25:23'),
(36, 4, NULL, 10, NULL, 'love', '2026-03-02 05:26:23'),
(38, 4, NULL, 11, NULL, 'love', '2026-03-02 10:55:13'),
(39, 4, 14, NULL, NULL, 'love', '2026-03-03 05:50:08'),
(40, 4, 16, NULL, NULL, 'haha', '2026-03-03 05:52:33'),
(41, 4, NULL, NULL, 36, 'haha', '2026-03-03 05:52:43'),
(42, 4, 17, NULL, NULL, 'haha', '2026-03-03 06:30:06'),
(43, 4, NULL, 20, NULL, 'haha', '2026-03-03 06:53:17'),
(44, 4, NULL, 22, NULL, 'haha', '2026-03-03 12:24:20'),
(45, 4, NULL, 23, NULL, 'haha', '2026-03-03 16:10:16'),
(46, 4, NULL, NULL, 38, 'haha', '2026-03-03 16:10:40'),
(47, 26, 19, NULL, NULL, 'sad', '2026-03-03 21:47:02');

-- --------------------------------------------------------

--
-- Structure de la table `reports`
--

CREATE TABLE `reports` (
  `id` int(11) NOT NULL,
  `reported_by_id` int(11) NOT NULL,
  `reported_user_id` int(11) DEFAULT NULL,
  `reported_post_id` int(11) DEFAULT NULL,
  `reported_comment_id` int(11) DEFAULT NULL,
  `report_type` enum('user','post','comment') NOT NULL,
  `reason` varchar(1000) NOT NULL,
  `created_at` datetime DEFAULT current_timestamp(),
  `status` enum('pending','resolved','rejected') DEFAULT 'pending'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Structure de la table `reviews`
--

CREATE TABLE `reviews` (
  `review_id` bigint(20) NOT NULL,
  `user_id` int(11) NOT NULL,
  `target_type` varchar(20) NOT NULL,
  `target_id` bigint(20) NOT NULL,
  `rating` int(11) NOT NULL CHECK (`rating` >= 1 and `rating` <= 5),
  `comment` text DEFAULT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Déchargement des données de la table `reviews`
--

INSERT INTO `reviews` (`review_id`, `user_id`, `target_type`, `target_id`, `rating`, `comment`, `created_at`) VALUES
(1, 6, 'DESTINATION', 126, 5, 'very good place i recommend', '2026-02-26 22:15:28'),
(2, 6, 'DESTINATION', 127, 3, 'good', '2026-02-26 22:53:55'),
(3, 25, 'DESTINATION', 126, 4, 'okkkk', '2026-02-27 03:18:42');

-- --------------------------------------------------------

--
-- Structure de la table `room`
--

CREATE TABLE `room` (
  `id` int(11) NOT NULL,
  `accommodation_id` int(11) DEFAULT NULL,
  `room_name` varchar(100) DEFAULT NULL,
  `room_type` varchar(50) DEFAULT NULL,
  `price_per_night` double DEFAULT NULL,
  `capacity` int(11) DEFAULT NULL,
  `size` decimal(8,2) DEFAULT NULL COMMENT 'Size in square meters',
  `amenities` text DEFAULT NULL,
  `is_available` tinyint(1) DEFAULT 1
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Déchargement des données de la table `room`
--

INSERT INTO `room` (`id`, `accommodation_id`, `room_name`, `room_type`, `price_per_night`, `capacity`, `size`, `amenities`, `is_available`) VALUES
(1, 1, 'Suite Room', 'Suite', 350, 4, 45.00, 'wifi,pool,balcony,sea_view', 1),
(2, 1, 'Premier Double Room', 'Premier Double', 550, 2, 55.00, 'Mini-bar, City View, Bathtub, High-speed Wi-Fi', 1),
(3, 1, 'Family Suite Room', 'Family Suite', 1200, 4, 85.00, 'Kitchenette, 2 Bedrooms, Balcony, Infinity Pool Access', 1),
(6, 3, 'Entire Villa Room', 'Entire Villa', 850, 6, 150.00, 'Private Pool, Kitchen, Sunset Terrace, Daily Housekeeping', 1),
(7, 4, 'Standard Capsule Room', 'Standard Capsule', 45, 1, 25.00, 'Shared Bath, USB Charging, Privacy Screen', 1),
(8, 4, 'Deluxe Capsule Room', 'Deluxe Capsule', 65, 1, 35.00, 'Noise-cancelling Walls, Larger Mattress', 1),
(9, 5, 'Royal Suite Room', 'Royal Suite', 12000, 2, 120.00, '24k Gold IPad, Private Cinema, Library, Rotating Bed', 1),
(11, 15, 'RaghedRoom', 'Deluxe', 60, 2, 34.00, NULL, 0),
(12, 15, 'hiba', 'Deluxe', 40, 50, 40.00, NULL, 0),
(13, 21, 'sgiux', 'Deluxe', 77, 6, 887.00, NULL, 0),
(15, 22, 'nouhaa', 'Family Room', 900, 7, 77.00, '', 1),
(16, 22, 'raghed', 'Suite', 664, 6, 24.00, 'Queen Bed,Bathrobes & Slippers,Air Conditioning,In-room Safe,Sea View', 1),
(17, 22, 'amal', 'Suite', 700, 3, 66.00, 'Soundproofing,Telephone,Work Desk,Pool View', 1),
(18, 21, 'raghedd', 'Family Room', 400, 77, 90.00, 'Queen Bed,Sofa Bed,Bathrobes & Slippers,Netflix,WiFi,Telephone,Air Conditioning,Heating,Ceiling Fan,Seating Area,Iron & Ironing Board,Sea View,Garden View', 1),
(19, 20, 'fatma', 'Suite', 500, 66, 88.00, 'Blackout Curtains,Bathtub,Cable/Satellite TV,Heating,Wake-up Service,Balcony/Terrace,Sea View,Pool View', 1),
(20, 20, 'hiba', 'Family Room', 650, 5, 66.00, 'King Bed,Blackout Curtains,Soundproofing,Private Bathroom,Free Toiletries,Bathrobes & Slippers,Heating,Sea View', 1),
(21, 20, 'NOUR', 'Standard', 777, 2, 45.00, 'Twin Beds,Private Bathroom,Bathtub,Free Toiletries,In-room Safe,Sea View', 1),
(22, 5, 'pariss', 'Presidential Suite', 1500, 4, 33.00, 'Soundproofing,Bathrobes & Slippers,Cable/Satellite TV,Heating,Mini-bar,Balcony/Terrace,Sea View', 1),
(23, 4, 'Capsule', 'Suite', 965, 3, 77.00, 'Soundproofing,Free Toiletries,Air Conditioning,Iron & Ironing Board,City View', 1),
(24, 10, 'luxuary room', 'Deluxe', 780, 4, 77.00, 'Blackout Curtains,Soundproofing,Rain Shower,Flat-screen TV,Cable/Satellite TV,Netflix,WiFi,Telephone,Air Conditioning,Heating,Mini-bar,Work Desk,Seating Area,Coffee/Tea Maker,Minibar/Fridge,Balcony/Terrace,Mountain View,Pool View', 1),
(25, 8, 'KING', 'Deluxe', 1470, 2, 60.00, 'King Bed,Soundproofing,Private Bathroom,Bathtub,Bathrobes & Slippers,Netflix,WiFi,Air Conditioning,Heating,Mini-bar,Coffee/Tea Maker,Minibar/Fridge,Iron & Ironing Board,Balcony/Terrace,City View', 1),
(30, 29, 'luxury room', 'Suite', 1500, 2, 15.00, 'King Bed,Blackout Curtains,Soundproofing,Private Bathroom,Hairdryer,Free Toiletries,Flat-screen TV,Cable/Satellite TV,Netflix,WiFi,Telephone,Air Conditioning,Heating,Mini-bar,In-room Safe,Seating Area,Minibar/Fridge,Wake-up Service,Balcony/Terrace,Sea View,Pool View', 1);

-- --------------------------------------------------------

--
-- Structure de la table `room_images`
--

CREATE TABLE `room_images` (
  `id` int(11) NOT NULL,
  `room_id` int(11) NOT NULL,
  `file_name` varchar(255) NOT NULL,
  `file_path` varchar(1024) NOT NULL,
  `mime_type` varchar(100) DEFAULT NULL,
  `file_size_bytes` bigint(20) UNSIGNED DEFAULT NULL,
  `width` int(10) UNSIGNED DEFAULT NULL,
  `height` int(10) UNSIGNED DEFAULT NULL,
  `is_primary` tinyint(1) NOT NULL DEFAULT 0,
  `display_order` int(11) NOT NULL DEFAULT 0,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp(),
  `updated_at` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Déchargement des données de la table `room_images`
--

INSERT INTO `room_images` (`id`, `room_id`, `file_name`, `file_path`, `mime_type`, `file_size_bytes`, `width`, `height`, `is_primary`, `display_order`, `created_at`, `updated_at`) VALUES
(1, 16, 'Capture d\'écran 2026-02-14 204016.png', '/uploads/images/rooms/16/2518ce54-56ba-4a69-9c82-e2392395a05b.png', 'image/png', 37135, 740, 282, 0, 3, '2026-02-18 19:13:45', '2026-02-19 08:30:15'),
(2, 16, 'Capture d\'écran 2026-02-14 194356.png', '/uploads/images/rooms/16/50c3cc73-61f4-4cdb-ba7b-b69882df4b07.png', 'image/png', 23199, 797, 340, 0, 1, '2026-02-18 19:13:45', '2026-02-19 08:30:15'),
(3, 16, 'Capture d\'écran 2026-02-12 221953.png', '/uploads/images/rooms/16/d3d223f9-6095-4ec1-aedd-ad1cf572cdbf.png', 'image/png', 538752, 1201, 903, 1, 2, '2026-02-18 19:13:45', '2026-02-19 08:30:14'),
(16, 15, '6c2e39063952d4a33aa7465a924985d1.jpg', '/uploads/images/rooms/15/9cc41da7-074d-4307-9d11-1172e51759c3.jpg', 'image/jpeg', 93689, 1200, 795, 0, 4, '2026-02-19 10:37:42', '2026-02-19 20:42:40'),
(17, 15, '23ce1ae338aebf7100a58f9dbf59113f.jpg', '/uploads/images/rooms/15/61cbe3e2-96b5-49a8-a73f-978682b12b8d.jpg', 'image/jpeg', 79418, 856, 611, 0, 3, '2026-02-19 10:38:02', '2026-02-19 20:42:40'),
(18, 15, '69e2b9f53f419fa29b435638f3d6bad2.jpg', '/uploads/images/rooms/15/071f94ac-7589-45b3-932e-84bf380d2c26.jpg', 'image/jpeg', 90617, 1200, 675, 0, 2, '2026-02-19 10:38:19', '2026-02-19 20:42:42'),
(19, 15, 'b3d5d410ef5fc69712dc68676d5a42dd.jpg', '/uploads/images/rooms/15/5c9499e1-8c60-4ede-87ae-630d9b84f3f7.jpg', 'image/jpeg', 145902, 1000, 563, 1, 1, '2026-02-19 10:38:37', '2026-02-19 20:42:42'),
(23, 30, 'room.jpg', '/uploads/images/rooms/30/e3ebcc6c-0917-494a-9c69-249476243d0d.jpg', 'image/jpeg', 38291, 701, 394, 0, 1, '2026-02-24 21:56:51', '2026-03-01 04:15:43'),
(24, 30, 'roomm.jpg', '/uploads/images/rooms/30/4827a174-e102-4d81-9587-79cff09238a0.jpg', 'image/jpeg', 38726, 701, 394, 0, 2, '2026-02-24 21:56:51', '2026-02-24 21:56:51'),
(25, 30, 'spaa.jpg', '/uploads/images/rooms/30/afc0bbdc-c7c7-4c91-98b6-1bfde198a9fd.jpg', 'image/jpeg', 131322, 1200, 630, 1, 3, '2026-02-24 21:56:51', '2026-03-01 04:15:43');

-- --------------------------------------------------------

--
-- Structure de la table `saved_posts`
--

CREATE TABLE `saved_posts` (
  `id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `post_id` int(11) NOT NULL,
  `saved_at` datetime DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Structure de la table `schedule`
--

CREATE TABLE `schedule` (
  `schedule_id` int(11) NOT NULL,
  `transport_id` int(11) NOT NULL,
  `departure_destination_id` int(11) NOT NULL,
  `arrival_destination_id` int(11) NOT NULL,
  `departure_datetime` datetime DEFAULT NULL,
  `arrival_datetime` datetime DEFAULT NULL,
  `rental_start` datetime DEFAULT NULL,
  `rental_end` datetime DEFAULT NULL,
  `travel_class` enum('ECONOMY','PREMIUM','BUSINESS','FIRST') DEFAULT NULL,
  `price_multiplier` double DEFAULT 1,
  `status` enum('ON_TIME','DELAYED','CANCELLED') DEFAULT 'ON_TIME',
  `delay_minutes` int(11) DEFAULT 0,
  `ai_demand_score` double DEFAULT NULL,
  `created_at` datetime DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Déchargement des données de la table `schedule`
--

INSERT INTO `schedule` (`schedule_id`, `transport_id`, `departure_destination_id`, `arrival_destination_id`, `departure_datetime`, `arrival_datetime`, `rental_start`, `rental_end`, `travel_class`, `price_multiplier`, `status`, `delay_minutes`, `ai_demand_score`, `created_at`) VALUES
(33, 26, 2, 2, '2025-02-02 18:18:00', '2026-02-02 12:12:00', NULL, NULL, 'ECONOMY', 1, 'CANCELLED', 30, 0, '2026-02-25 17:54:37'),
(44, 30, 1, 1, '2027-02-07 08:00:00', '2028-02-06 10:00:00', NULL, NULL, 'ECONOMY', 1, 'ON_TIME', 0, 0, '2026-02-26 06:43:35'),
(50, 40, 2, 2, '2028-02-05 08:00:00', '2040-02-05 10:00:00', NULL, NULL, 'ECONOMY', 1, 'ON_TIME', 0, 0, '2026-02-26 12:03:24'),
(51, 40, 1, 1, '2031-02-02 08:00:00', '2032-02-15 10:00:00', NULL, NULL, 'ECONOMY', 1, 'ON_TIME', 0, 0, '2026-02-26 12:19:17'),
(52, 30, 1, 1, '2027-02-05 08:00:00', '2028-02-05 10:00:00', NULL, NULL, 'ECONOMY', 1, 'ON_TIME', 0, 0, '2026-02-26 12:28:23'),
(53, 26, 1, 1, NULL, NULL, '2028-01-09 00:00:00', '2029-02-04 23:59:00', 'ECONOMY', 1, 'ON_TIME', 0, 0, '2026-02-26 12:51:58'),
(54, 44, 1, 1, '2027-02-07 08:00:00', '2028-01-02 10:00:00', NULL, NULL, 'ECONOMY', 1, 'ON_TIME', 0, 0, '2026-02-26 13:50:35'),
(55, 26, 2, 2, NULL, NULL, '2026-03-05 00:00:00', '2026-03-06 23:59:00', 'PREMIUM', 1, 'ON_TIME', 0, 0, '2026-02-26 19:56:43'),
(56, 26, 1, 1, NULL, NULL, '2026-03-08 00:00:00', '2027-03-09 23:59:00', 'FIRST', 1, 'ON_TIME', 0, 0, '2026-02-26 20:52:12'),
(57, 26, 1, 1, NULL, NULL, '2026-04-22 00:00:00', '2026-04-23 23:59:00', 'BUSINESS', 1, 'ON_TIME', 0, 0, '2026-02-26 21:33:58'),
(58, 26, 1, 1, NULL, NULL, '2026-03-11 00:00:00', '2026-03-12 23:59:00', 'BUSINESS', 1, 'ON_TIME', 0, 0, '2026-02-26 22:22:30');

-- --------------------------------------------------------

--
-- Structure de la table `shares`
--

CREATE TABLE `shares` (
  `id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `post_id` int(11) DEFAULT NULL,
  `travel_story_id` int(11) DEFAULT NULL,
  `created_at` datetime DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Déchargement des données de la table `shares`
--

INSERT INTO `shares` (`id`, `user_id`, `post_id`, `travel_story_id`, `created_at`) VALUES
(1, 4, 11, NULL, '2026-03-01 04:50:44'),
(2, 4, 11, NULL, '2026-03-01 04:50:55'),
(3, 4, 11, NULL, '2026-03-01 04:51:05'),
(5, 4, 3, NULL, '2026-03-01 05:40:11');

-- --------------------------------------------------------

--
-- Structure de la table `stories`
--

CREATE TABLE `stories` (
  `id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `image_url` varchar(500) NOT NULL,
  `caption` varchar(255) DEFAULT NULL,
  `created_at` datetime NOT NULL DEFAULT current_timestamp(),
  `expires_at` datetime NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Déchargement des données de la table `stories`
--

INSERT INTO `stories` (`id`, `user_id`, `image_url`, `caption`, `created_at`, `expires_at`) VALUES
(9, 4, 'C:\\Users\\nmour\\OneDrive\\Pictures\\image_0.jpg', NULL, '2026-03-03 17:40:48', '2026-03-04 17:40:48'),
(10, 26, 'C:\\Users\\USER\\Downloads\\santorini-villa.jpg', 'look at this villa in santorini', '2026-03-03 21:50:16', '2026-03-04 21:50:16');

-- --------------------------------------------------------

--
-- Structure de la table `transport`
--

CREATE TABLE `transport` (
  `transport_id` int(11) NOT NULL,
  `transport_type` enum('FLIGHT','VEHICLE') NOT NULL,
  `provider_name` varchar(100) DEFAULT NULL,
  `vehicle_model` varchar(100) DEFAULT NULL,
  `base_price` decimal(10,2) DEFAULT NULL,
  `capacity` int(11) DEFAULT NULL,
  `available_units` int(11) DEFAULT NULL,
  `sustainability_rating` double DEFAULT NULL,
  `amenities` text DEFAULT NULL,
  `image_url` varchar(255) DEFAULT NULL,
  `dynamic_price_factor` double DEFAULT 1,
  `is_active` tinyint(1) DEFAULT 1,
  `created_at` datetime DEFAULT current_timestamp(),
  `updated_at` datetime DEFAULT current_timestamp() ON UPDATE current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Déchargement des données de la table `transport`
--

INSERT INTO `transport` (`transport_id`, `transport_type`, `provider_name`, `vehicle_model`, `base_price`, `capacity`, `available_units`, `sustainability_rating`, `amenities`, `image_url`, `dynamic_price_factor`, `is_active`, `created_at`, `updated_at`) VALUES
(26, 'VEHICLE', 'tunisair', '57', 20.00, 200, 200, 4.5, '', '', 1, 1, '2026-02-19 06:26:38', '2026-02-26 11:40:11'),
(28, 'FLIGHT', 'g', '5', 25.00, 2, 2, 2, '', 'https://tse4.mm.bing.net/th/id/OIP.CIac5BAbYhrvBddLTcoNxAHaEW?rs=1&pid=ImgDetMain&o=7&rm=3', 1, 1, '2026-02-19 12:50:27', '2026-02-19 12:50:27'),
(30, 'FLIGHT', 'france', '74', 2.00, 180, 2, 5, '', '', 1, 1, '2026-02-25 22:00:01', '2026-02-25 22:00:01'),
(31, 'FLIGHT', 'hhh', '32', 200.00, 20, 180, 4, '', '', 1, 1, '2026-02-25 23:29:52', '2026-02-25 23:29:52'),
(33, 'FLIGHT', 'jg', '68', 53.00, 2, 22, 5, '', '', 1, 1, '2026-02-26 04:20:16', '2026-02-26 04:20:16'),
(34, 'FLIGHT', 'jhgc', 'kjgv', 354.00, 35, 67, 5, '', 'C:\\Users\\pc\\Downloads\\upperbar.jpeg', 1, 1, '2026-02-26 05:39:05', '2026-02-27 19:12:18'),
(36, 'FLIGHT', 'j', '5', 5.00, 58, 58, 5, '', '', 1, 1, '2026-02-26 06:24:27', '2026-02-26 06:24:27'),
(37, 'FLIGHT', 'jhgc', '357', 24.00, 354, 32, 5, '', '', 1, 1, '2026-02-26 06:35:48', '2026-02-26 06:35:48'),
(39, 'FLIGHT', 'tunisair', '2', 10.00, 2, 32, 22, '', '', 1, 1, '2026-02-26 07:24:20', '2026-02-26 07:24:20'),
(40, 'FLIGHT', 'france', '65', 65.00, 35, 35, 5, '', '', 1, 1, '2026-02-26 08:21:34', '2026-02-26 08:21:34'),
(41, 'FLIGHT', 'nv', '54324', 257.00, 253, 24, 2, '', 'C:\\Users\\pc\\Downloads\\Capture d\'écran 2026-02-26 080527.png', 1, 1, '2026-02-26 10:04:19', '2026-02-26 10:04:19'),
(42, 'FLIGHT', ',hgc', '654', 654.00, 324, 65, 5, '', '', 1, 1, '2026-02-26 11:03:18', '2026-02-26 11:03:18'),
(43, 'FLIGHT', 'vb', '254', 35.00, 385, 541, 5, '', '', 1, 1, '2026-02-26 11:40:01', '2026-02-26 11:40:01'),
(44, 'FLIGHT', 'xv', 'dg', 8000000.00, 2, 1, 5, '', '', 1, 1, '2026-02-26 13:48:06', '2026-02-26 13:48:06');

-- --------------------------------------------------------

--
-- Structure de la table `travel_story`
--

CREATE TABLE `travel_story` (
  `travel_story_id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `destination_id` bigint(20) UNSIGNED DEFAULT NULL,
  `title` varchar(255) NOT NULL,
  `summary` text DEFAULT NULL,
  `start_date` date DEFAULT NULL,
  `end_date` date DEFAULT NULL,
  `travel_type` varchar(20) DEFAULT NULL,
  `travel_style` varchar(20) DEFAULT NULL,
  `overall_rating` tinyint(3) UNSIGNED DEFAULT NULL,
  `would_recommend` tinyint(1) NOT NULL DEFAULT 1,
  `would_go_again` tinyint(1) NOT NULL DEFAULT 0,
  `tips` text DEFAULT NULL,
  `currency` varchar(8) NOT NULL DEFAULT 'TND',
  `total_budget` decimal(10,2) DEFAULT NULL,
  `budget_json` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL CHECK (json_valid(`budget_json`)),
  `tags_json` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL CHECK (json_valid(`tags_json`)),
  `must_visit_json` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL CHECK (json_valid(`must_visit_json`)),
  `must_do_json` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL CHECK (json_valid(`must_do_json`)),
  `must_try_json` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL CHECK (json_valid(`must_try_json`)),
  `favorite_places_json` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL CHECK (json_valid(`favorite_places_json`)),
  `destination` varchar(255) DEFAULT NULL,
  `cover_image_url` varchar(500) DEFAULT NULL,
  `image_urls_json` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL CHECK (json_valid(`image_urls_json`)),
  `created_at` timestamp NOT NULL DEFAULT current_timestamp(),
  `updated_at` timestamp NULL DEFAULT NULL ON UPDATE current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Déchargement des données de la table `travel_story`
--

INSERT INTO `travel_story` (`travel_story_id`, `user_id`, `destination_id`, `title`, `summary`, `start_date`, `end_date`, `travel_type`, `travel_style`, `overall_rating`, `would_recommend`, `would_go_again`, `tips`, `currency`, `total_budget`, `budget_json`, `tags_json`, `must_visit_json`, `must_do_json`, `must_try_json`, `favorite_places_json`, `destination`, `cover_image_url`, `image_urls_json`, `created_at`, `updated_at`) VALUES
(2, 1, NULL, 'llll', NULL, NULL, NULL, NULL, NULL, NULL, 1, 0, NULL, 'TND', NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'njnh', NULL, NULL, '2026-02-18 22:17:48', NULL),
(3, 1, NULL, 'guvvg', NULL, NULL, NULL, NULL, NULL, NULL, 1, 0, NULL, 'TND', NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'buh', NULL, NULL, '2026-02-18 23:20:48', NULL),
(6, 1, NULL, 'Trip', NULL, NULL, NULL, NULL, NULL, NULL, 1, 0, NULL, 'TND', NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'Bali', NULL, NULL, '2026-02-19 05:56:09', '2026-02-19 07:44:37'),
(8, 2, NULL, 'srgrsg', NULL, NULL, NULL, NULL, NULL, NULL, 1, 0, NULL, 'TND', NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'sgs', NULL, NULL, '2026-02-19 11:36:43', NULL),
(10, 4, NULL, 'flu', NULL, NULL, NULL, NULL, NULL, NULL, 1, 0, NULL, 'TND', NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'hbm', NULL, NULL, '2026-03-02 03:26:16', NULL),
(11, 4, NULL, 'hello', '', NULL, NULL, '', 'Mid-range', 5, 1, 0, '', 'TND', NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'bali', NULL, NULL, '2026-03-02 08:55:08', '2026-03-02 09:20:15'),
(12, 4, NULL, 'Best travel', 'it was really nice', '2026-03-03', NULL, 'Solo', 'Cultural', 2, 1, 1, '', 'TND', NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'bali', NULL, NULL, '2026-03-02 09:19:50', NULL),
(13, 4, NULL, 'refe', 'aefaef', '2026-03-04', '2026-03-03', 'Leisure', 'Budget', 5, 1, 0, 'e\'fraef', 'TND', NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'eaf', NULL, NULL, '2026-03-02 10:17:49', NULL),
(15, 4, NULL, 'efef', 'sfs', NULL, NULL, 'Leisure', 'Mid-range', 5, 1, 0, 'sfse', 'TND', NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'sfsf', 'C:\\Users\\nmour\\Downloads\\bo.png', '[\"C:\\\\Users\\\\nmour\\\\Downloads\\\\bo.png\"]', '2026-03-03 03:51:15', NULL),
(17, 4, NULL, 'zef', 'zef', NULL, NULL, NULL, NULL, 5, 1, 0, 'zef', 'TND', NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'zef', NULL, NULL, '2026-03-03 04:23:26', NULL),
(18, 4, NULL, 'hey', 'hey', NULL, NULL, NULL, NULL, 0, 1, 0, 'sgfg', 'TND', NULL, NULL, NULL, NULL, NULL, NULL, NULL, 's<f', 'C:\\Users\\nmour\\Downloads\\best-free-travel-images-main-image-hd-op.jpg', '[\"C:\\\\Users\\\\nmour\\\\Downloads\\\\best-free-travel-images-main-image-hd-op.jpg\"]', '2026-03-03 04:25:04', NULL),
(19, 4, NULL, 'zef', 'zEF', '2026-02-26', '2026-03-13', 'Leisure', 'Budget', 5, 1, 1, 'ZEF', 'TND', NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'zefz', 'C:\\Users\\nmour\\Downloads\\best-free-travel-images-main-image-hd-op.jpg', '[\"C:\\\\Users\\\\nmour\\\\Downloads\\\\best-free-travel-images-main-image-hd-op.jpg\",\"C:\\\\Users\\\\nmour\\\\Downloads\\\\dcc5544b-c8dd-4c3b-a2b4-b0f91b240544.jpg\"]', '2026-03-03 04:29:16', NULL),
(20, 4, NULL, 'rtr', 'hhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhh', NULL, NULL, 'Leisure', 'Budget', 5, 1, 0, 'twhrth', 'TND', NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'hhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhh', 'C:\\Users\\nmour\\Downloads\\best-free-travel-images-main-image-hd-op.jpg', '[\"C:\\\\Users\\\\nmour\\\\Downloads\\\\best-free-travel-images-main-image-hd-op.jpg\",\"C:\\\\Users\\\\nmour\\\\Downloads\\\\dcc5544b-c8dd-4c3b-a2b4-b0f91b240544.jpg\",\"C:\\\\Users\\\\nmour\\\\OneDrive\\\\Pictures\\\\image_0.jpg\"]', '2026-03-03 04:40:38', '2026-03-03 06:41:57'),
(21, 4, NULL, 'trip to bali', 'there was a time where it was fine', NULL, NULL, 'Leisure', 'Budget', 5, 1, 0, 'pack lightly because of', 'TND', NULL, NULL, '[\"then\",\"thither\",\"therein\",\"yonder\",\"here\",\"meanwhile\",\"at-that-place\",\"in-that-location\",\"in-that-respect\",\"on-that-point\"]', NULL, NULL, NULL, NULL, 'Bali', NULL, NULL, '2026-03-03 06:34:10', NULL),
(22, 4, NULL, 'cfbvdf', 'no do', NULL, NULL, 'Leisure', 'Budget', 5, 1, 0, 'thick isd', 'TND', NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'bali', 'C:\\Users\\nmour\\OneDrive\\Pictures\\image_0.jpg', '[\"C:\\\\Users\\\\nmour\\\\OneDrive\\\\Pictures\\\\image_0.jpg\",\"C:\\\\Users\\\\nmour\\\\OneDrive\\\\Pictures\\\\Screenshots\\\\Screenshot 2025-12-14 024138.png\"]', '2026-03-03 10:20:48', '2026-03-03 10:21:09'),
(23, 4, NULL, 'zef', 'zEF', NULL, NULL, 'Leisure', 'Budget', 5, 1, 0, 'Zefzf', 'TND', NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'zefze', 'C:\\Users\\nmour\\OneDrive\\Pictures\\image_0.jpg', '[\"C:\\\\Users\\\\nmour\\\\OneDrive\\\\Pictures\\\\image_0.jpg\",\"C:\\\\Users\\\\nmour\\\\OneDrive\\\\Pictures\\\\Screenshots\\\\Screenshot 2026-03-03 081124.png\",\"C:\\\\Users\\\\nmour\\\\Downloads\\\\dcc5544b-c8dd-4c3b-a2b4-b0f91b240544.jpg\"]', '2026-03-03 12:21:43', NULL),
(24, 4, NULL, 'nour', 'traveling', '2026-03-13', '2026-03-17', 'Leisure', 'Budget', 5, 1, 0, 'jsfel<jsf', 'TND', NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'Andorra', 'C:\\Users\\nmour\\OneDrive\\Pictures\\image_0.jpg', '[\"C:\\\\Users\\\\nmour\\\\OneDrive\\\\Pictures\\\\image_0.jpg\"]', '2026-03-03 15:21:54', NULL),
(25, 4, NULL, 'scvsdv', 'eqc<cd', '2026-03-13', NULL, 'Leisure', 'Budget', 5, 1, 0, 'dvq<vdf', 'TND', NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'Algeria', 'C:\\Users\\nmour\\Downloads\\ai.png', '[\"C:\\\\Users\\\\nmour\\\\Downloads\\\\ai.png\"]', '2026-03-03 15:29:25', '2026-03-03 15:31:44'),
(26, 4, NULL, 'zefz', 'Zefzef', NULL, NULL, 'Leisure', 'Budget', 5, 1, 0, 'ZEFZFE', 'TND', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2026-03-03 15:33:59', NULL),
(27, 26, NULL, 'argentina  was amazinggg', 'you have to eat grilled meat there', '2026-03-20', '2026-03-28', 'Road Trip', 'Cultural', 5, 1, 0, 'dont go to touristic traps', 'TND', NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'Argentina', 'C:\\Users\\USER\\Downloads\\4e14a84e89a5d9d09b0409001e339cb8.jpg', '[\"C:\\\\Users\\\\USER\\\\Downloads\\\\4e14a84e89a5d9d09b0409001e339cb8.jpg\"]', '2026-03-03 20:48:54', '2026-03-03 20:49:27');

-- --------------------------------------------------------

--
-- Structure de la table `user`
--

CREATE TABLE `user` (
  `user_id` int(11) NOT NULL,
  `first_name` varchar(50) NOT NULL,
  `last_name` varchar(50) NOT NULL,
  `email` varchar(100) NOT NULL,
  `password` varchar(255) NOT NULL,
  `phone_number` varchar(20) DEFAULT NULL,
  `birth_year` varchar(20) DEFAULT NULL,
  `gender` enum('Male','Female') DEFAULT NULL,
  `profile_picture` varchar(255) DEFAULT NULL,
  `avatar_id` varchar(50) DEFAULT NULL,
  `role` enum('admin','user','adminDestination','adminAccomodation','adminTransport','adminBlog','adminOffers') NOT NULL DEFAULT 'user',
  `status` enum('active','banned','suspended','pending_verification') DEFAULT 'pending_verification',
  `email_verified` tinyint(1) DEFAULT 0,
  `verification_token` varchar(100) DEFAULT NULL,
  `reset_token` varchar(100) DEFAULT NULL,
  `last_login` datetime DEFAULT NULL,
  `created_at` datetime DEFAULT current_timestamp(),
  `updated_at` datetime DEFAULT current_timestamp() ON UPDATE current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Déchargement des données de la table `user`
--

INSERT INTO `user` (`user_id`, `first_name`, `last_name`, `email`, `password`, `phone_number`, `birth_year`, `gender`, `profile_picture`, `avatar_id`, `role`, `status`, `email_verified`, `verification_token`, `reset_token`, `last_login`, `created_at`, `updated_at`) VALUES
(1, 'Hiba', 'Dkhil', 'test1@tripx.com', '$2y$10$abcdefghijklmnopqrstuvwxyz1234567890', '+1234567890', '1990-1999', 'Female', NULL, NULL, 'admin', 'active', 1, NULL, NULL, NULL, '2026-02-19 22:57:02', '2026-02-06 12:10:07'),
(2, 'testtest', 'test', 'demo.user2@tripx.com', 't', NULL, NULL, NULL, NULL, NULL, 'user', 'pending_verification', 0, NULL, NULL, NULL, '2026-02-20 22:05:06', '2026-02-11 16:06:58'),
(3, 'user', 'user', 'u@gmail.com', 'uuuuuuuu', NULL, NULL, NULL, NULL, NULL, 'admin', 'pending_verification', 0, NULL, NULL, NULL, '2026-02-09 22:32:16', '2026-02-18 17:21:25'),
(4, 'Demo', 'User4', 'demo.user4@tripx.com', 'dev_hash', '+21640000004', NULL, NULL, NULL, NULL, 'user', 'active', 1, NULL, NULL, NULL, '2026-02-23 12:14:31', '2026-02-23 12:14:31'),
(5, 'Demo', 'User5', 'demo.user5@tripx.com', 'dev_hash', '+21640000005', NULL, NULL, NULL, NULL, 'user', 'active', 1, NULL, NULL, NULL, '2026-02-23 12:14:31', '2026-02-23 12:14:31'),
(6, 'lily', 'dom', 'demo.user6@tripx.com', 'Lily1234', '', '2005-07-06', 'Female', NULL, 'big-smile:Bella', 'user', 'pending_verification', 0, NULL, NULL, NULL, '2026-02-23 12:14:31', '2026-02-26 13:40:16'),
(7, 'john', 'fow', 'john@gmail.com', '$2a$10$OlMFxwWyweHEaP4aF1iwt.JlNeCA//ieO21oYc.w97Ug25PiTVJ4e', '58990164', '1980-1989', 'Male', NULL, NULL, 'user', 'pending_verification', 0, NULL, NULL, NULL, '2026-02-18 18:02:50', '2026-02-18 18:31:27'),
(8, 'admin', 'admin', 'admin@gmail.com', '$2a$10$cXeSWiw1cp9boswFnJSwQOpc/kfmyn7QLkbNU4V/f/AktNei4Ds/C', NULL, '1960-1969', 'Male', NULL, NULL, 'admin', 'pending_verification', 0, NULL, NULL, NULL, '2026-02-18 19:19:52', '2026-02-18 19:28:37'),
(9, 'sarra', 'lnw', 'sarra@gmail.com', '$2a$10$QNlXFmIRcqrVTY0tHDMSnONIDugcexzIL9t0HtZQheaR67XZVUfbS', '12345678', '1990-1999', 'Female', NULL, 'big-smile:Ethan', 'user', 'pending_verification', 0, NULL, NULL, NULL, '2026-02-18 19:29:35', '2026-02-23 19:51:43'),
(10, 'hiba', 'aaaaaa', 'hibadk@gmail.com', '$2a$10$32.pNkJbLB69aTd0XHv0kO2cs2t2f6k5GX3Z3J7xMPyi75.6c73I.', NULL, '1970-1979', 'Female', NULL, NULL, 'user', 'pending_verification', 0, NULL, NULL, NULL, '2026-02-18 19:40:58', '2026-02-23 15:59:25'),
(11, 'validation', 'aa', 'comptetest740@gmail.com', '$2a$10$v38Mqv4wjU9hWuhXUYFtR.C1oGez8A8mG3DPS7EeddFWpvqZXaCMm', NULL, '1960-1969', 'Male', NULL, NULL, 'user', 'active', 0, NULL, NULL, NULL, '2026-02-18 20:47:36', '2026-02-24 14:36:37'),
(12, 'raghed', 'selmi', 'raghed@gmail.com', '$2a$10$LPFePUmLHRxWTyK77xR/ru9jkcypJX0pKoXXtSvMYIBHa4ZZhGLM.', '', '2000-2010', 'Female', NULL, NULL, 'adminAccomodation', 'pending_verification', 0, NULL, NULL, NULL, '2026-02-19 09:22:38', '2026-02-21 12:38:44'),
(14, 'seif', 'meddeb', 'meddeb780@gmail.com', '$2a$10$FadoGtkzyHeAfUP.2S8YCOiKOrsS3tPafh.BAh1qj5Ta4JKq7Fr4.', NULL, '2000-2010', 'Male', NULL, NULL, 'adminDestination', 'pending_verification', 0, NULL, NULL, NULL, '2026-02-21 12:16:49', '2026-02-21 12:21:20'),
(15, 'fatma', 'mdaghi', 'fatma@gmail.com', '$2a$10$pYkL0oPjGElvHHovxPZphu3Ll9gbrcLw6zbioARSTDU4KNl9yt1Uy', NULL, '1980-1989', 'Female', NULL, NULL, 'adminTransport', 'pending_verification', 0, NULL, NULL, NULL, '2026-02-21 12:28:36', '2026-02-21 12:39:08'),
(18, 'islem', 'medfai', 'islem@gmail.com', '$2a$10$Gkkl41.DFVPrJeLFd6CVT.488eO.RxEISDvnxFLx/TX/L9XC6N.2q', NULL, '1990-1999', 'Male', NULL, NULL, 'adminOffers', 'pending_verification', 0, NULL, NULL, NULL, '2026-02-21 12:37:23', '2026-02-21 12:39:49'),
(19, 'Nour', 'Mourali', 'nour@gmail.com', '$2a$10$/5IxbGhZ3IZiZYgUKtgia.xhlkDM3zVVtnwg897UVOwD51fhhwjmu', NULL, '1970-1979', 'Female', NULL, NULL, 'adminBlog', 'pending_verification', 0, NULL, NULL, NULL, '2026-02-21 12:33:55', '2026-02-21 12:34:27'),
(21, 'hiba', 'dkhil', 'hibadkhil01@gmail.com', '$2a$10$ph4Qp1g6WFOPiW47eZeZXeSGfLuEz9NJFiqmqBAYmNbqGWuECZ88a', '', '2000-2010', 'Female', NULL, 'big-smile:Charlie', 'user', 'active', 0, NULL, NULL, NULL, '2026-02-23 15:59:41', '2026-02-27 01:14:20'),
(22, 'john', 'farw', 'johndoe110@gmail.com', '$2a$10$I8Giv/XNSc6MxE.eA0Nm4.NeoN5YwLPrO54s2qVSWQ1P51S8azF16', NULL, '1970-1979', 'Male', NULL, 'big-smile:Ian', 'user', 'active', 0, NULL, NULL, NULL, '2026-02-24 22:55:06', '2026-02-27 00:32:29'),
(24, 'Hiba', 'dkhil', 'hibadkhil7@gmail.com', '$2a$10$hE.73xArBUZ4ruf44/IEmu7U3RERkVFh854eNPdB8Fx7..RydtIO.', NULL, '2000-2010', 'Female', NULL, NULL, 'user', 'pending_verification', 0, NULL, NULL, NULL, '2026-02-25 20:25:06', '2026-02-25 20:25:33'),
(25, 'nada', 'selmi', 'nadaselmi@yahoo.fr', '$2a$10$WRpQnypwEv5iFZvzr5/GMOEpvDOtqXFKAmS8IRUGQl7K75ISdidi2', '', '1992-05-28', 'Female', NULL, 'big-smile:George', 'user', 'pending_verification', 0, NULL, NULL, NULL, '2026-02-27 04:09:40', '2026-02-27 04:35:57'),
(26, 'amine', 'selmi', 'amineselmi@gmail.com', '$2a$10$xbbmfkmqPx0SkwM2WMiDee.uc5LDWHa89QXkwnMAtGLZLzrM.nhu2', NULL, '1990-1999', 'Male', NULL, 'big-smile:Adrian', 'user', 'pending_verification', 0, NULL, NULL, NULL, '2026-02-27 06:10:13', '2026-02-27 06:23:11'),
(27, 'Rihab', 'mahfoudhi', 'rihab@gmail.com', '$2a$10$CosCrCL.LUzNzKjnI9GnZ.ExzvQA0G5zG.25QRJLiSueiaJrzKSPq', NULL, '1990-1999', 'Female', NULL, NULL, 'user', 'pending_verification', 0, NULL, NULL, NULL, '2026-02-27 11:39:10', '2026-02-27 11:40:07'),
(28, 'Asma', 'selmi', 'Asma@gmail.com', '$2a$10$i3wl39W266.p1GH4G.3ZQe9qZbYR2IhHWJxC1GJgSplpauSh3pMbW', '', '2026-02-20', 'Female', NULL, 'big-smile:Brian', 'user', 'pending_verification', 0, NULL, NULL, NULL, '2026-02-27 12:29:39', '2026-02-27 13:17:59'),
(29, 'Raghd', 'selmi', 'raghdselmi@gmail.com', '$2a$10$z2wbbIrRxCH0OPzX0K5J7.LROr2IwiqSp6f7wh885TfGIG79sSnSa', NULL, '1980-1989', 'Female', NULL, 'big-smile:Ethan', 'user', 'pending_verification', 0, NULL, NULL, NULL, '2026-03-01 03:44:27', '2026-03-01 03:45:18');

-- --------------------------------------------------------

--
-- Structure de la table `userpreferences`
--

CREATE TABLE `userpreferences` (
  `preference_id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `budget_min_per_night` decimal(10,2) DEFAULT NULL COMMENT 'Minimum nightly budget',
  `budget_max_per_night` decimal(10,2) DEFAULT NULL COMMENT 'Maximum nightly budget',
  `priorities` text DEFAULT NULL,
  `location_preferences` text DEFAULT NULL,
  `accommodation_types` text DEFAULT NULL,
  `style_preferences` text DEFAULT NULL,
  `dietary_restrictions` text DEFAULT NULL,
  `preferred_climate` text DEFAULT NULL,
  `travel_pace` enum('Relaxed','Moderate','Fast-paced') DEFAULT 'Moderate' COMMENT 'Trip intensity',
  `group_type` enum('Solo','Couple','Family','Friends','Business') DEFAULT NULL COMMENT 'Travel companions',
  `accessibility_needs` tinyint(1) DEFAULT 0 COMMENT 'Requires accessibility features',
  `created_at` datetime DEFAULT current_timestamp(),
  `updated_at` datetime DEFAULT current_timestamp() ON UPDATE current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Déchargement des données de la table `userpreferences`
--

INSERT INTO `userpreferences` (`preference_id`, `user_id`, `budget_min_per_night`, `budget_max_per_night`, `priorities`, `location_preferences`, `accommodation_types`, `style_preferences`, `dietary_restrictions`, `preferred_climate`, `travel_pace`, `group_type`, `accessibility_needs`, `created_at`, `updated_at`) VALUES
(1, 1, 59.00, 118.00, '[\"Activities\", \"Wellness\", \"Food and Drinks\", \"Amenities\"]', '[\"City Center\", \"Beachfront\"]', '[\"Hotel\", \"Villa\", \"Vacation Rental\", \"Cabin\"]', '[\"Minimalist\", \"Urban\", \"Luxury\", \"Mediterranean\"]', '[\"Vegetarian\", \"Gluten-Free\"]', '[\"Tropical\", \"Mediterranean\", \"Temperate\", \"Mountain\"]', 'Moderate', 'Couple', 0, '2026-02-06 12:33:17', '2026-02-06 12:33:17'),
(2, 6, 150.00, 1000.00, 'Activities,Family Friendly,Pet-Friendly,Food and Drinks', 'Beachfront,Mountain', 'Villa,Hotel,Camping,Hostel', 'Urban,Mediterranean,Vintage,Bohemian', 'Vegan', 'Tropical,Oceanic,Temperate', 'Fast-paced', 'Family', 0, '2026-02-18 17:35:46', '2026-02-20 14:17:03'),
(3, 7, 50.00, 1936.81, 'Wellness,Amenities,Parking,Business Facilities', 'Mountain View,Countryside', 'Hotel,Villa,Boat/Yacht,Vacation Rental', 'Classic,Luxury,Urban,Futuristic', 'Nut Allergies', 'Desert', 'Relaxed', 'Solo', 0, '2026-02-18 18:08:58', '2026-02-18 18:35:30'),
(4, 8, 50.00, 1514.58, 'Amenities', 'City Center', 'Cabin', 'Vintage', 'Seafood Allergies', 'Desert', 'Fast-paced', 'Family', 0, '2026-02-18 19:20:23', '2026-02-18 19:20:23'),
(5, 9, 50.00, 2000.00, 'Business Facilities,Food and Drinks,Activities,Amenities', 'Beachfront,City Center', 'Villa,Hotel,Resort,Vacation Rental', 'Minimalist,Urban,Tropical,Mediterranean', 'None', 'Tropical,Desert,Cold/Arctic,Oceanic', 'Fast-paced', 'Couple', 0, '2026-02-18 19:30:16', '2026-02-18 19:32:12'),
(6, 10, 50.00, 1400.69, 'Family Friendly,Pet-Friendly,Amenities,Activities', 'Mountain View,Beachfront', 'Hostel,Villa,Resort,Hotel', 'Vintage,Mediterranean,Urban,Rustic', 'Nut Allergies,Halal,Vegetarian,Sugar-Free', 'Desert,Tropical,Temperate', 'Moderate', 'Friends', 0, '2026-02-18 19:41:33', '2026-02-18 19:41:33'),
(7, 11, 50.00, 195.14, 'Business Facilities,I dont care', 'City Center', 'Camping', 'Classic', 'Low-Sodium', 'Cold/Arctic', 'Moderate', 'Business', 0, '2026-02-18 20:48:08', '2026-02-18 20:48:08'),
(8, 12, 139.24, 500.00, 'Wellness,Business Facilities', 'Beachfront', 'Villa,Hotel', 'Bohemian', 'Vegan,Nut Allergies,Halal', 'Tropical', 'Fast-paced', 'Couple', 0, '2026-02-19 09:23:53', '2026-02-19 09:28:54'),
(10, 14, 461.46, 1995.14, 'Food and Drinks,Amenities,Activities,Family Friendly', 'Beachfront,City Center', 'Villa,Resort,Hostel,Boat/Yacht,Vacation Rental,Hotel', 'Minimalist,Luxury,Urban,Vintage', 'Halal', 'Tropical,Mountain,Oceanic', 'Moderate', 'Couple', 0, '2026-02-21 12:17:35', '2026-02-21 12:17:35'),
(11, 15, 279.51, 1414.58, 'Family Friendly,I dont care,Parking,Pet-Friendly', 'Beachfront,City Center', 'Villa,Resort,Hotel,Cabin', 'Luxury,Urban,Tropical,Vintage', 'Vegan', 'Desert,Tropical,Cold/Arctic', 'Relaxed', 'Solo', 0, '2026-02-21 12:29:09', '2026-02-21 12:29:09'),
(13, 18, 50.00, 1292.36, 'Pet-Friendly,Amenities,Activities,Family Friendly', 'Beachfront,City Center', 'Resort,Hotel,Hostel', 'Luxury,Tropical,Vintage,Bohemian', 'None', 'Desert,Oceanic,Mountain', 'Fast-paced', 'Family', 0, '2026-02-21 12:37:50', '2026-02-21 12:37:50'),
(14, 21, 50.00, 1814.58, 'Activities,Food and Drinks,Wellness,Amenities', 'Countryside,City Center', 'Resort,Hostel,Hotel,Villa', 'Minimalist,Mediterranean,Classic,Vintage', 'None,Halal', 'Cold/Arctic', 'Relaxed', 'Couple', 0, '2026-02-23 16:00:50', '2026-02-23 16:07:42'),
(15, 22, 50.00, 1645.14, 'Food and Drinks,Wellness,Amenities,Pet-Friendly', 'Mountain View', 'Villa,Hotel,Cabin', 'Rustic,Traditional,Classic', 'Vegetarian', 'Cold/Arctic,Temperate,Oceanic', 'Fast-paced', 'Friends', 0, '2026-02-24 22:55:46', '2026-02-24 22:55:46'),
(17, 24, 222.57, 1728.47, 'Wellness,Activities,Amenities,Food and Drinks', 'Beachfront,City Center', 'Villa,Cabin,Camping', 'Bohemian,Luxury,Urban,Tropical', 'Seafood Allergies,Vegan', 'Cold/Arctic,Oceanic', 'Moderate', 'Family', 0, '2026-02-25 20:25:33', '2026-02-25 20:25:33'),
(18, 28, 500.00, 682.99, 'Activities,Family Friendly,Business Facilities,Pet-Friendly', 'Mountain View', 'Cabin,Resort', 'Vintage,Mediterranean,Bohemian', 'Vegan,Nut Allergies,Seafood Allergies', 'Cold/Arctic,Desert,Tropical', 'Relaxed', 'Family', 0, '2026-02-27 12:30:23', '2026-02-27 12:30:23'),
(19, 26, 50.00, 200.00, 'I dont care', 'City Center', 'Hotel', 'Classic', 'None', 'Temperate', 'Moderate', 'Solo', 0, '2026-02-27 20:39:59', '2026-02-27 20:39:59'),
(20, 29, 500.00, 623.26, 'Business Facilities,Pet-Friendly', 'Beachfront', 'Boat/Yacht', 'Mediterranean,Traditional', 'Low-Sodium,Nut Allergies,Halal', 'Oceanic', 'Relaxed', 'Solo', 0, '2026-03-01 03:45:13', '2026-03-01 03:45:13');

-- --------------------------------------------------------

--
-- Structure de la table `user_activity_log`
--

CREATE TABLE `user_activity_log` (
  `log_id` int(11) NOT NULL,
  `user_id` int(11) DEFAULT NULL,
  `activity_type` varchar(50) NOT NULL,
  `target_id` bigint(20) DEFAULT NULL,
  `target_type` varchar(50) DEFAULT NULL,
  `timestamp` datetime DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Déchargement des données de la table `user_activity_log`
--

INSERT INTO `user_activity_log` (`log_id`, `user_id`, `activity_type`, `target_id`, `target_type`, `timestamp`) VALUES
(1, 6, 'VISIT', NULL, 'PAGE:HOME', '2026-02-24 17:21:58'),
(2, 6, 'CLICK', 117, 'DESTINATION', '2026-02-24 17:22:25'),
(3, 6, 'VISIT', NULL, 'PAGE:HOME', '2026-02-24 17:22:44'),
(4, 6, 'VISIT', NULL, 'PAGE:PROFILE', '2026-02-24 17:22:45'),
(5, 6, 'VISIT', NULL, 'PAGE:HOME', '2026-02-24 17:23:17'),
(6, 6, 'SEARCH', NULL, 'QUERY:Canada', '2026-02-24 17:23:35'),
(7, 6, 'VISIT', NULL, 'PAGE:HOME', '2026-02-24 17:24:29'),
(8, 6, 'VISIT', NULL, 'PAGE:PROFILE', '2026-02-24 17:24:54'),
(9, 6, 'VISIT', NULL, 'PAGE:HOME', '2026-02-24 17:25:25'),
(10, 6, 'VISIT', NULL, 'PAGE:PROFILE', '2026-02-24 17:27:01'),
(11, 6, 'VISIT', NULL, 'PAGE:HOME', '2026-02-24 17:27:17'),
(12, 6, 'VISIT', NULL, 'PAGE:PROFILE', '2026-02-24 17:27:18'),
(13, 6, 'VISIT', NULL, 'PAGE:HOME', '2026-02-24 17:27:23'),
(14, 6, 'VISIT', NULL, 'PAGE:HOME', '2026-02-24 17:28:33'),
(15, 6, 'VISIT', NULL, 'PAGE:PROFILE', '2026-02-24 17:28:39'),
(16, 6, 'VISIT', NULL, 'PAGE:HOME', '2026-02-24 17:28:56'),
(17, 6, 'USE', NULL, 'FEATURE:AI_CHAT', '2026-02-24 17:28:59'),
(18, 6, 'USE', NULL, 'FEATURE:AI_CHAT', '2026-02-24 17:29:01'),
(19, 6, 'USE', NULL, 'FEATURE:AI_CHAT', '2026-02-24 17:29:03'),
(20, 6, 'USE', NULL, 'FEATURE:AI_CHAT', '2026-02-24 17:29:05'),
(21, 6, 'USE', NULL, 'FEATURE:CLIMATE_MATCH', '2026-02-24 17:29:08'),
(22, 6, 'VISIT', NULL, 'PAGE:HOME', '2026-02-24 17:29:45'),
(23, 6, 'VISIT', NULL, 'PAGE:PROFILE', '2026-02-24 17:29:48'),
(24, 6, 'VISIT', NULL, 'PAGE:HOME', '2026-02-24 17:30:16'),
(25, 6, 'SEARCH', NULL, 'QUERY:Tuscan Countryside', '2026-02-24 17:30:23'),
(26, 6, 'VISIT', NULL, 'PAGE:HOME', '2026-02-24 17:30:25'),
(27, 6, 'SEARCH', NULL, 'QUERY:Swiss Alps', '2026-02-24 17:30:30'),
(28, 6, 'VISIT', NULL, 'PAGE:HOME', '2026-02-24 17:30:33'),
(29, 6, 'VISIT', NULL, 'PAGE:PROFILE', '2026-02-24 17:30:34'),
(30, 6, 'VISIT', NULL, 'PAGE:HOME', '2026-02-24 17:39:22'),
(31, 6, 'VISIT', NULL, 'PAGE:PROFILE', '2026-02-24 17:39:33'),
(32, 6, 'VISIT', NULL, 'PAGE:HOME', '2026-02-24 17:40:42'),
(33, 6, 'VISIT', NULL, 'PAGE:HOME', '2026-02-24 17:40:56'),
(34, 6, 'VISIT', NULL, 'PAGE:HOME', '2026-02-24 17:41:00'),
(35, 6, 'VISIT', NULL, 'PAGE:HOME', '2026-02-24 17:41:10'),
(36, 22, 'VISIT', NULL, 'PAGE:HOME', '2026-02-24 22:55:46'),
(37, 22, 'VISIT', NULL, 'PAGE:HOME', '2026-02-24 22:58:09'),
(38, 22, 'USE', NULL, 'FEATURE:AI_CHAT', '2026-02-24 22:58:16'),
(39, 22, 'VISIT', NULL, 'PAGE:HOME', '2026-02-24 23:00:48'),
(40, 22, 'USE', NULL, 'FEATURE:AI_CHAT', '2026-02-24 23:00:54'),
(41, 22, 'USE', NULL, 'FEATURE:AI_CHAT', '2026-02-24 23:01:30'),
(42, 22, 'USE', NULL, 'FEATURE:AI_CHAT', '2026-02-24 23:02:08'),
(43, 22, 'USE', NULL, 'FEATURE:CLIMATE_MATCH', '2026-02-24 23:02:31'),
(44, 22, 'USE', NULL, 'FEATURE:TRIP_PLANNER', '2026-02-24 23:03:42'),
(45, 22, 'VISIT', NULL, 'PAGE:HOME', '2026-02-24 23:04:26'),
(46, 22, 'VISIT', NULL, 'PAGE:HOME', '2026-02-24 23:04:49'),
(47, 22, 'VISIT', NULL, 'PAGE:PROFILE', '2026-02-24 23:04:54'),
(48, 22, 'VISIT', NULL, 'PAGE:HOME', '2026-02-24 23:05:04'),
(49, 22, 'VISIT', NULL, 'PAGE:PROFILE', '2026-02-24 23:05:07'),
(50, 22, 'VISIT', NULL, 'PAGE:HOME', '2026-02-24 23:06:08'),
(51, 22, 'VISIT', NULL, 'PAGE:HOME', '2026-02-24 23:07:12'),
(52, 22, 'VISIT', NULL, 'PAGE:PROFILE', '2026-02-24 23:07:13'),
(53, 22, 'VISIT', NULL, 'PAGE:HOME', '2026-02-24 23:07:25'),
(54, 21, 'VISIT', NULL, 'PAGE:HOME', '2026-02-25 15:31:56'),
(55, 21, 'USE', NULL, 'FEATURE:AI_CHAT', '2026-02-25 15:32:04'),
(56, 21, 'VISIT', NULL, 'PAGE:PROFILE', '2026-02-25 15:32:22'),
(57, 21, 'VISIT', NULL, 'PAGE:HOME', '2026-02-25 15:32:48'),
(58, 21, 'VISIT', NULL, 'PAGE:PROFILE', '2026-02-25 15:32:50'),
(59, 21, 'VISIT', NULL, 'PAGE:HOME', '2026-02-25 15:33:14'),
(60, 21, 'VISIT', NULL, 'PAGE:HOME', '2026-02-25 15:33:20'),
(61, 21, 'VISIT', NULL, 'PAGE:PROFILE', '2026-02-25 15:33:21'),
(62, 21, 'VISIT', NULL, 'PAGE:HOME', '2026-02-25 15:33:41'),
(63, 21, 'VISIT', NULL, 'PAGE:HOME', '2026-02-25 15:33:56'),
(64, 21, 'VISIT', NULL, 'PAGE:HOME', '2026-02-25 15:34:01'),
(65, 6, 'VISIT', NULL, 'PAGE:HOME', '2026-02-25 16:44:35'),
(66, 6, 'VISIT', NULL, 'PAGE:HOME', '2026-02-25 16:57:22'),
(73, 24, 'VISIT', NULL, 'PAGE:HOME', '2026-02-25 20:25:34'),
(74, 24, 'USE', NULL, 'FEATURE:AI_CHAT', '2026-02-25 20:30:29'),
(75, 24, 'USE', NULL, 'FEATURE:AI_CHAT', '2026-02-25 20:30:51'),
(76, 6, 'VISIT', NULL, 'PAGE:HOME', '2026-02-25 21:28:16'),
(77, 6, 'VISIT', NULL, 'PAGE:PROFILE', '2026-02-25 21:28:35'),
(78, 6, 'VISIT', NULL, 'PAGE:HOME', '2026-02-25 21:28:37'),
(79, 6, 'VISIT', NULL, 'PAGE:HOME', '2026-02-26 10:21:24'),
(80, 6, 'VISIT', NULL, 'PAGE:HOME', '2026-02-26 10:21:51'),
(81, 6, 'VISIT', NULL, 'PAGE:HOME', '2026-02-26 13:36:23'),
(82, 6, 'USE', NULL, 'FEATURE:AI_CHAT', '2026-02-26 13:37:28'),
(83, 6, 'USE', NULL, 'FEATURE:CLIMATE_MATCH', '2026-02-26 13:38:28'),
(84, 6, 'USE', NULL, 'FEATURE:TRIP_PLANNER', '2026-02-26 13:39:23'),
(85, 6, 'VISIT', NULL, 'PAGE:PROFILE', '2026-02-26 13:40:09'),
(86, 6, 'VISIT', NULL, 'PAGE:HOME', '2026-02-26 13:40:20'),
(87, 6, 'VISIT', NULL, 'PAGE:PROFILE', '2026-02-26 13:40:22'),
(88, 6, 'VISIT', NULL, 'PAGE:HOME', '2026-02-26 13:41:45'),
(89, 6, 'VISIT', NULL, 'PAGE:HOME', '2026-02-26 19:14:11'),
(90, 6, 'VISIT', NULL, 'PAGE:PROFILE', '2026-02-26 19:14:14'),
(91, 6, 'VISIT', NULL, 'PAGE:HOME', '2026-02-26 19:14:20'),
(92, 21, 'VISIT', NULL, 'PAGE:HOME', '2026-02-26 19:19:27'),
(93, 21, 'VISIT', NULL, 'PAGE:PROFILE', '2026-02-26 19:19:29'),
(94, 21, 'VISIT', NULL, 'PAGE:HOME', '2026-02-26 19:19:35'),
(95, 6, 'VISIT', NULL, 'PAGE:HOME', '2026-02-26 19:40:48'),
(96, 6, 'VISIT', NULL, 'PAGE:PROFILE', '2026-02-26 19:40:51'),
(97, 6, 'VISIT', NULL, 'PAGE:HOME', '2026-02-26 19:40:56'),
(98, 6, 'VISIT', NULL, 'PAGE:HOME', '2026-02-26 21:16:03'),
(99, 6, 'VISIT', NULL, 'PAGE:PROFILE', '2026-02-26 21:16:13'),
(100, 6, 'VISIT', NULL, 'PAGE:HOME', '2026-02-26 21:16:17'),
(101, 6, 'VISIT', NULL, 'PAGE:PROFILE', '2026-02-26 21:16:19'),
(102, 6, 'VISIT', NULL, 'PAGE:HOME', '2026-02-26 21:16:25'),
(103, 21, 'VISIT', NULL, 'PAGE:HOME', '2026-02-26 21:20:36'),
(104, 21, 'VISIT', NULL, 'PAGE:PROFILE', '2026-02-26 21:20:44'),
(105, 21, 'VISIT', NULL, 'PAGE:HOME', '2026-02-26 21:20:49'),
(106, 6, 'VISIT', NULL, 'PAGE:HOME', '2026-02-26 21:21:43'),
(107, 9, 'VISIT', NULL, 'PAGE:HOME', '2026-02-26 21:57:12'),
(108, 9, 'VISIT', NULL, 'PAGE:PROFILE', '2026-02-26 21:57:14'),
(109, 9, 'VISIT', NULL, 'PAGE:HOME', '2026-02-26 21:57:23'),
(110, 9, 'VISIT', NULL, 'PAGE:PROFILE', '2026-02-26 21:57:29'),
(111, 9, 'VISIT', NULL, 'PAGE:HOME', '2026-02-26 21:57:31'),
(112, 6, 'VISIT', NULL, 'PAGE:HOME', '2026-02-26 21:58:54'),
(113, 6, 'VISIT', NULL, 'PAGE:HOME', '2026-02-26 21:59:01'),
(114, 9, 'VISIT', NULL, 'PAGE:HOME', '2026-02-26 22:16:08'),
(115, 9, 'VISIT', NULL, 'PAGE:PROFILE', '2026-02-26 22:16:36'),
(116, 9, 'VISIT', NULL, 'PAGE:HOME', '2026-02-26 22:16:43'),
(117, 6, 'VISIT', NULL, 'PAGE:HOME', '2026-02-26 22:48:02'),
(118, 6, 'VISIT', NULL, 'PAGE:HOME', '2026-02-26 23:16:45'),
(119, 6, 'VISIT', NULL, 'PAGE:HOME', '2026-02-26 23:18:03'),
(120, 6, 'VISIT', NULL, 'PAGE:HOME', '2026-02-26 23:18:17'),
(121, 6, 'VISIT', NULL, 'PAGE:PROFILE', '2026-02-26 23:18:23'),
(122, 6, 'VISIT', NULL, 'PAGE:HOME', '2026-02-26 23:18:51'),
(123, 9, 'VISIT', NULL, 'PAGE:HOME', '2026-02-26 23:39:14'),
(124, 9, 'VISIT', NULL, 'PAGE:HOME', '2026-02-26 23:44:16'),
(125, 6, 'VISIT', NULL, 'PAGE:HOME', '2026-02-27 00:13:53'),
(126, 6, 'VISIT', NULL, 'PAGE:HOME', '2026-02-27 00:23:27'),
(127, 6, 'VISIT', NULL, 'PAGE:HOME', '2026-02-27 00:53:19'),
(128, 6, 'VISIT', NULL, 'PAGE:PROFILE', '2026-02-27 00:53:21'),
(129, 6, 'VISIT', NULL, 'PAGE:HOME', '2026-02-27 00:53:22'),
(130, 6, 'VISIT', NULL, 'PAGE:PROFILE', '2026-02-27 00:53:23'),
(131, 6, 'VISIT', NULL, 'PAGE:HOME', '2026-02-27 00:53:26'),
(132, 21, 'VISIT', NULL, 'PAGE:HOME', '2026-02-27 01:11:31'),
(133, 21, 'USE', NULL, 'FEATURE:CLIMATE_MATCH', '2026-02-27 01:11:34'),
(134, 21, 'USE', NULL, 'FEATURE:TRIP_PLANNER', '2026-02-27 01:12:04'),
(135, 21, 'USE', NULL, 'FEATURE:AI_CHAT', '2026-02-27 01:13:30'),
(136, 21, 'SEARCH', NULL, 'QUERY:Tuscan Countryside', '2026-02-27 01:14:04'),
(137, 21, 'VISIT', NULL, 'PAGE:HOME', '2026-02-27 01:14:10'),
(138, 21, 'VISIT', NULL, 'PAGE:PROFILE', '2026-02-27 01:14:18'),
(139, 21, 'VISIT', NULL, 'PAGE:HOME', '2026-02-27 01:14:22'),
(140, 21, 'VISIT', NULL, 'PAGE:PROFILE', '2026-02-27 01:14:29'),
(141, 21, 'VISIT', NULL, 'PAGE:HOME', '2026-02-27 01:14:42'),
(142, 21, 'VISIT', NULL, 'PAGE:PROFILE', '2026-02-27 01:14:43'),
(143, 21, 'VISIT', NULL, 'PAGE:HOME', '2026-02-27 01:14:57'),
(144, 27, 'VISIT', NULL, 'PAGE:HOME', '2026-02-27 11:40:08'),
(145, 28, 'VISIT', NULL, 'PAGE:HOME', '2026-02-27 12:30:24'),
(146, 28, 'USE', NULL, 'FEATURE:TRIP_PLANNER', '2026-02-27 12:30:48'),
(147, 28, 'USE', NULL, 'FEATURE:CLIMATE_MATCH', '2026-02-27 12:31:30'),
(148, 28, 'VISIT', NULL, 'PAGE:HOME', '2026-02-27 12:33:48'),
(149, 28, 'USE', NULL, 'FEATURE:CLIMATE_MATCH', '2026-02-27 12:33:54'),
(150, 28, 'USE', NULL, 'FEATURE:CLIMATE_MATCH', '2026-02-27 12:33:55'),
(151, 28, 'USE', NULL, 'FEATURE:CLIMATE_MATCH', '2026-02-27 12:33:55'),
(152, 28, 'USE', NULL, 'FEATURE:CLIMATE_MATCH', '2026-02-27 12:33:56'),
(153, 28, 'VISIT', NULL, 'PAGE:HOME', '2026-02-27 12:34:39'),
(154, 28, 'VISIT', NULL, 'PAGE:HOME', '2026-02-27 12:34:48'),
(155, 28, 'VISIT', NULL, 'PAGE:PROFILE', '2026-02-27 12:34:50'),
(156, 28, 'VISIT', NULL, 'PAGE:HOME', '2026-02-27 12:35:40'),
(157, 28, 'SEARCH', NULL, 'QUERY:Paris', '2026-02-27 12:36:12'),
(158, 28, 'VISIT', NULL, 'PAGE:HOME', '2026-02-27 12:36:23'),
(159, 28, 'VISIT', NULL, 'PAGE:PROFILE', '2026-02-27 12:36:26'),
(160, 28, 'VISIT', NULL, 'PAGE:HOME', '2026-02-27 12:36:53'),
(161, 28, 'VISIT', NULL, 'PAGE:PROFILE', '2026-02-27 12:36:55'),
(162, 28, 'VISIT', NULL, 'PAGE:HOME', '2026-02-27 12:36:58'),
(163, 28, 'VISIT', NULL, 'PAGE:HOME', '2026-02-27 12:38:39'),
(164, 28, 'USE', NULL, 'FEATURE:AI_CHAT', '2026-02-27 12:39:05'),
(165, 28, 'USE', NULL, 'FEATURE:CLIMATE_MATCH', '2026-02-27 12:39:48'),
(166, 28, 'USE', NULL, 'FEATURE:CLIMATE_MATCH', '2026-02-27 12:39:50'),
(167, 28, 'VISIT', NULL, 'PAGE:PROFILE', '2026-02-27 12:41:27'),
(168, 28, 'VISIT', NULL, 'PAGE:HOME', '2026-02-27 12:41:48'),
(169, 28, 'VISIT', NULL, 'PAGE:HOME', '2026-02-27 12:42:37'),
(170, 28, 'VISIT', NULL, 'PAGE:PROFILE', '2026-02-27 12:42:59'),
(171, 28, 'VISIT', NULL, 'PAGE:HOME', '2026-02-27 12:43:15'),
(172, 28, 'VISIT', NULL, 'PAGE:HOME', '2026-02-27 13:01:14'),
(173, 28, 'VISIT', NULL, 'PAGE:PROFILE', '2026-02-27 13:01:48'),
(174, 28, 'VISIT', NULL, 'PAGE:PROFILE', '2026-02-27 13:01:48'),
(175, 28, 'VISIT', NULL, 'PAGE:HOME', '2026-02-27 13:02:10'),
(176, 28, 'VISIT', NULL, 'PAGE:PROFILE', '2026-02-27 13:02:15'),
(177, 28, 'VISIT', NULL, 'PAGE:PROFILE', '2026-02-27 13:02:15'),
(178, 28, 'VISIT', NULL, 'PAGE:HOME', '2026-02-27 13:02:18'),
(179, 28, 'VISIT', NULL, 'PAGE:PROFILE', '2026-02-27 13:02:19'),
(180, 28, 'VISIT', NULL, 'PAGE:PROFILE', '2026-02-27 13:02:19'),
(181, 28, 'VISIT', NULL, 'PAGE:HOME', '2026-02-27 13:02:24'),
(182, 28, 'VISIT', NULL, 'PAGE:PROFILE', '2026-02-27 13:02:43'),
(183, 28, 'VISIT', NULL, 'PAGE:PROFILE', '2026-02-27 13:02:43'),
(184, 28, 'VISIT', NULL, 'PAGE:HOME', '2026-02-27 13:02:57'),
(185, 28, 'VISIT', NULL, 'PAGE:HOME', '2026-02-27 13:05:00'),
(186, 28, 'VISIT', NULL, 'PAGE:PROFILE', '2026-02-27 13:05:54'),
(187, 28, 'VISIT', NULL, 'PAGE:PROFILE', '2026-02-27 13:05:54'),
(188, 28, 'VISIT', NULL, 'PAGE:HOME', '2026-02-27 13:06:27'),
(189, 28, 'VISIT', NULL, 'PAGE:PROFILE', '2026-02-27 13:06:29'),
(190, 28, 'VISIT', NULL, 'PAGE:PROFILE', '2026-02-27 13:06:29'),
(191, 28, 'VISIT', NULL, 'PAGE:HOME', '2026-02-27 13:06:40'),
(192, 28, 'VISIT', NULL, 'PAGE:PROFILE', '2026-02-27 13:06:42'),
(193, 28, 'VISIT', NULL, 'PAGE:PROFILE', '2026-02-27 13:06:42'),
(194, 28, 'VISIT', NULL, 'PAGE:HOME', '2026-02-27 13:06:46'),
(195, 28, 'VISIT', NULL, 'PAGE:PROFILE', '2026-02-27 13:06:48'),
(196, 28, 'VISIT', NULL, 'PAGE:PROFILE', '2026-02-27 13:06:48'),
(197, 28, 'VISIT', NULL, 'PAGE:HOME', '2026-02-27 13:06:56'),
(198, 28, 'VISIT', NULL, 'PAGE:HOME', '2026-02-27 13:14:48'),
(199, 28, 'VISIT', NULL, 'PAGE:PROFILE', '2026-02-27 13:15:05'),
(200, 28, 'VISIT', NULL, 'PAGE:PROFILE', '2026-02-27 13:15:05'),
(201, 28, 'VISIT', NULL, 'PAGE:HOME', '2026-02-27 13:15:40'),
(202, 28, 'VISIT', NULL, 'PAGE:HOME', '2026-02-27 13:15:48'),
(203, 28, 'VISIT', NULL, 'PAGE:PROFILE', '2026-02-27 13:15:53'),
(204, 28, 'VISIT', NULL, 'PAGE:PROFILE', '2026-02-27 13:15:53'),
(205, 28, 'VISIT', NULL, 'PAGE:HOME', '2026-02-27 13:15:58'),
(206, 28, 'VISIT', NULL, 'PAGE:HOME', '2026-02-27 13:16:52'),
(207, 28, 'VISIT', NULL, 'PAGE:HOME', '2026-02-27 13:17:05'),
(208, 28, 'VISIT', NULL, 'PAGE:PROFILE', '2026-02-27 13:17:53'),
(209, 28, 'VISIT', NULL, 'PAGE:PROFILE', '2026-02-27 13:17:53'),
(210, 28, 'VISIT', NULL, 'PAGE:HOME', '2026-02-27 13:18:07'),
(211, 28, 'VISIT', NULL, 'PAGE:HOME', '2026-02-27 13:18:14'),
(212, 28, 'VISIT', NULL, 'PAGE:HOME', '2026-02-27 13:18:23'),
(213, 28, 'VISIT', NULL, 'PAGE:HOME', '2026-02-27 13:18:34'),
(214, 28, 'VISIT', NULL, 'PAGE:HOME', '2026-02-27 13:18:43'),
(215, 28, 'VISIT', NULL, 'PAGE:HOME', '2026-02-27 13:19:20'),
(216, 28, 'VISIT', NULL, 'PAGE:PROFILE', '2026-02-27 13:19:48'),
(217, 28, 'VISIT', NULL, 'PAGE:PROFILE', '2026-02-27 13:19:48'),
(218, 28, 'VISIT', NULL, 'PAGE:HOME', '2026-02-27 13:19:55'),
(219, 28, 'VISIT', NULL, 'PAGE:HOME', '2026-02-27 13:21:23'),
(220, 26, 'VISIT', NULL, 'PAGE:HOME', '2026-02-27 20:17:10'),
(221, 26, 'VISIT', NULL, 'PAGE:HOME', '2026-02-27 20:17:32'),
(222, 26, 'VISIT', NULL, 'PAGE:HOME', '2026-02-27 20:17:33'),
(223, 26, 'VISIT', NULL, 'PAGE:HOME', '2026-02-27 20:18:21'),
(224, 26, 'VISIT', NULL, 'PAGE:HOME', '2026-02-27 20:18:36'),
(225, 26, 'VISIT', NULL, 'PAGE:HOME', '2026-02-27 20:18:37'),
(226, 26, 'VISIT', NULL, 'PAGE:HOME', '2026-02-27 20:18:38'),
(227, 26, 'VISIT', NULL, 'PAGE:HOME', '2026-02-27 20:19:34'),
(228, 26, 'VISIT', NULL, 'PAGE:HOME', '2026-02-27 20:37:27'),
(229, 26, 'VISIT', NULL, 'PAGE:TRANSPORT', '2026-02-27 20:37:43'),
(230, 26, 'VISIT', NULL, 'PAGE:TRANSPORT', '2026-02-27 20:38:02'),
(231, 26, 'VISIT', NULL, 'PAGE:TRANSPORT', '2026-02-27 20:38:25'),
(232, 26, 'VISIT', NULL, 'PAGE:PROFILE', '2026-02-27 20:39:59'),
(233, 26, 'VISIT', NULL, 'PAGE:PROFILE', '2026-02-27 20:39:59'),
(234, 26, 'VISIT', NULL, 'PAGE:HOME', '2026-02-27 20:40:04'),
(235, 26, 'VISIT', NULL, 'PAGE:TRANSPORT', '2026-02-27 20:40:08'),
(236, 26, 'VISIT', NULL, 'PAGE:HOME', '2026-02-27 20:40:27'),
(237, 26, 'VISIT', NULL, 'PAGE:HOME', '2026-02-27 20:42:44'),
(238, 26, 'VISIT', NULL, 'PAGE:TRANSPORT', '2026-02-27 20:42:50'),
(239, 26, 'VISIT', NULL, 'PAGE:HOME', '2026-02-27 20:43:38'),
(240, 26, 'VISIT', NULL, 'PAGE:HOME', '2026-02-27 20:57:41'),
(241, 26, 'VISIT', NULL, 'PAGE:TRANSPORT', '2026-02-27 20:57:53'),
(242, 26, 'VISIT', NULL, 'PAGE:TRANSPORT', '2026-02-27 20:58:00'),
(243, 26, 'VISIT', NULL, 'PAGE:TRANSPORT', '2026-02-27 20:58:26'),
(244, 26, 'VISIT', NULL, 'PAGE:HOME', '2026-02-27 20:58:39'),
(245, 26, 'VISIT', NULL, 'PAGE:HOME', '2026-02-27 20:59:01'),
(246, 26, 'VISIT', NULL, 'PAGE:TRANSPORT', '2026-02-27 20:59:07'),
(247, 26, 'VISIT', NULL, 'PAGE:HOME', '2026-02-27 21:01:43'),
(248, 26, 'VISIT', NULL, 'PAGE:TRANSPORT', '2026-02-27 21:01:46'),
(249, 26, 'VISIT', NULL, 'PAGE:HOME', '2026-02-27 21:11:14'),
(250, 26, 'VISIT', NULL, 'PAGE:TRANSPORT', '2026-02-27 21:33:42'),
(251, 26, 'VISIT', NULL, 'PAGE:TRANSPORT', '2026-02-27 21:35:22'),
(252, 26, 'VISIT', NULL, 'PAGE:HOME', '2026-02-27 22:06:26'),
(253, 26, 'VISIT', NULL, 'PAGE:TRANSPORT', '2026-02-27 22:06:54'),
(254, 26, 'VISIT', NULL, 'PAGE:HOME', '2026-02-27 22:07:01'),
(255, 26, 'VISIT', NULL, 'PAGE:TRANSPORT', '2026-02-27 22:07:12'),
(256, 26, 'VISIT', NULL, 'PAGE:TRANSPORT', '2026-02-27 22:07:14'),
(257, 26, 'VISIT', NULL, 'PAGE:HOME', '2026-02-27 22:07:50'),
(258, 26, 'VISIT', NULL, 'PAGE:TRANSPORT', '2026-02-27 22:08:05'),
(259, 26, 'VISIT', NULL, 'PAGE:HOME', '2026-02-27 22:25:20'),
(260, 26, 'VISIT', NULL, 'PAGE:HOME', '2026-02-27 22:25:34'),
(261, 26, 'VISIT', NULL, 'PAGE:TRANSPORT', '2026-02-27 22:30:58'),
(262, 26, 'VISIT', NULL, 'PAGE:HOME', '2026-02-27 22:35:55'),
(263, 26, 'VISIT', NULL, 'PAGE:HOME', '2026-02-27 22:38:45'),
(264, 26, 'VISIT', NULL, 'PAGE:HOME', '2026-02-27 22:38:45'),
(265, 26, 'VISIT', NULL, 'PAGE:TRANSPORT', '2026-02-27 22:38:47'),
(266, 26, 'VISIT', NULL, 'PAGE:HOME', '2026-02-27 22:39:57'),
(267, 26, 'VISIT', NULL, 'PAGE:HOME', '2026-02-27 22:39:57'),
(268, 26, 'VISIT', NULL, 'PAGE:HOME', '2026-02-28 00:24:34'),
(269, 26, 'VISIT', NULL, 'PAGE:TRANSPORT', '2026-02-28 00:27:26'),
(270, 26, 'VISIT', NULL, 'PAGE:HOME', '2026-02-28 00:28:02'),
(271, 26, 'VISIT', NULL, 'PAGE:HOME', '2026-02-28 00:44:46'),
(272, 26, 'VISIT', NULL, 'PAGE:HOME', '2026-03-01 03:35:14'),
(273, 26, 'VISIT', NULL, 'PAGE:HOME', '2026-03-01 03:35:38'),
(274, 26, 'VISIT', NULL, 'PAGE:TRANSPORT', '2026-03-01 03:35:52'),
(275, 26, 'VISIT', NULL, 'PAGE:HOME', '2026-03-01 03:36:05'),
(276, 26, 'VISIT', NULL, 'PAGE:TRANSPORT', '2026-03-01 03:36:14'),
(277, 26, 'VISIT', NULL, 'PAGE:HOME', '2026-03-01 03:36:20'),
(278, 26, 'VISIT', NULL, 'PAGE:HOME', '2026-03-01 03:39:13'),
(279, 29, 'VISIT', NULL, 'PAGE:HOME', '2026-03-01 03:45:13'),
(280, 29, 'VISIT', NULL, 'PAGE:PROFILE', '2026-03-01 03:45:17'),
(281, 29, 'VISIT', NULL, 'PAGE:PROFILE', '2026-03-01 03:45:17'),
(282, 29, 'VISIT', NULL, 'PAGE:HOME', '2026-03-01 03:45:22'),
(283, 29, 'VISIT', NULL, 'PAGE:PROFILE', '2026-03-01 03:45:26'),
(284, 29, 'VISIT', NULL, 'PAGE:PROFILE', '2026-03-01 03:45:26'),
(285, 29, 'VISIT', NULL, 'PAGE:PROFILE', '2026-03-01 03:45:42'),
(286, 29, 'VISIT', NULL, 'PAGE:PROFILE', '2026-03-01 03:45:42'),
(287, 29, 'VISIT', NULL, 'PAGE:HOME', '2026-03-01 03:46:01'),
(288, 29, 'VISIT', NULL, 'PAGE:TRANSPORT', '2026-03-01 03:46:10'),
(289, 26, 'VISIT', NULL, 'PAGE:HOME', '2026-03-01 12:28:09'),
(290, 26, 'VISIT', NULL, 'PAGE:TRANSPORT', '2026-03-01 12:28:51'),
(291, 26, 'VISIT', NULL, 'PAGE:HOME', '2026-03-01 12:29:55'),
(292, 26, 'VISIT', NULL, 'PAGE:PROFILE', '2026-03-01 12:30:10'),
(293, 26, 'VISIT', NULL, 'PAGE:PROFILE', '2026-03-01 12:30:10'),
(294, 26, 'VISIT', NULL, 'PAGE:PROFILE', '2026-03-01 12:32:53'),
(295, 26, 'VISIT', NULL, 'PAGE:PROFILE', '2026-03-01 12:32:53'),
(296, 26, 'VISIT', NULL, 'PAGE:HOME', '2026-03-01 12:32:56'),
(297, 26, 'VISIT', NULL, 'PAGE:TRANSPORT', '2026-03-01 12:33:01'),
(298, 26, 'VISIT', NULL, 'PAGE:HOME', '2026-03-01 12:34:47'),
(299, 26, 'VISIT', NULL, 'PAGE:TRANSPORT', '2026-03-01 12:35:12'),
(300, 26, 'VISIT', NULL, 'PAGE:HOME', '2026-03-01 12:35:27'),
(301, 26, 'VISIT', NULL, 'PAGE:HOME', '2026-03-01 12:45:27'),
(302, 26, 'VISIT', NULL, 'PAGE:TRANSPORT', '2026-03-01 12:48:09'),
(303, 26, 'VISIT', NULL, 'PAGE:HOME', '2026-03-01 12:49:41'),
(304, 26, 'VISIT', NULL, 'PAGE:HOME', '2026-03-01 12:49:45'),
(305, 26, 'VISIT', NULL, 'PAGE:TRANSPORT', '2026-03-01 12:49:53'),
(306, 26, 'VISIT', NULL, 'PAGE:PROFILE', '2026-03-01 12:49:59'),
(307, 26, 'VISIT', NULL, 'PAGE:PROFILE', '2026-03-01 12:49:59'),
(308, 26, 'VISIT', NULL, 'PAGE:HOME', '2026-03-01 12:50:08'),
(309, 26, 'VISIT', NULL, 'PAGE:HOME', '2026-03-01 13:01:36'),
(310, 26, 'VISIT', NULL, 'PAGE:TRANSPORT', '2026-03-01 13:02:03'),
(311, 26, 'VISIT', NULL, 'PAGE:TRANSPORT', '2026-03-01 13:02:36'),
(312, 26, 'VISIT', NULL, 'PAGE:HOME', '2026-03-01 13:05:02'),
(313, 26, 'VISIT', NULL, 'PAGE:PROFILE', '2026-03-01 13:06:34'),
(314, 26, 'VISIT', NULL, 'PAGE:PROFILE', '2026-03-01 13:06:41'),
(315, 26, 'VISIT', NULL, 'PAGE:HOME', '2026-03-01 13:08:48'),
(316, 26, 'VISIT', NULL, 'PAGE:TRANSPORT', '2026-03-01 13:09:04'),
(317, 26, 'VISIT', NULL, 'PAGE:HOME', '2026-03-01 13:09:06'),
(318, 26, 'VISIT', NULL, 'PAGE:PROFILE', '2026-03-01 13:09:18'),
(319, 26, 'VISIT', NULL, 'PAGE:PROFILE', '2026-03-01 13:09:18'),
(320, 26, 'VISIT', NULL, 'PAGE:HOME', '2026-03-01 13:09:25'),
(321, 26, 'VISIT', NULL, 'PAGE:HOME', '2026-03-02 10:04:07'),
(322, 26, 'VISIT', NULL, 'PAGE:TRANSPORT', '2026-03-02 10:04:52'),
(323, 26, 'VISIT', NULL, 'PAGE:TRANSPORT', '2026-03-02 10:05:18'),
(324, 26, 'VISIT', NULL, 'PAGE:PROFILE', '2026-03-02 10:05:48'),
(325, 26, 'VISIT', NULL, 'PAGE:PROFILE', '2026-03-02 10:05:48'),
(326, 26, 'VISIT', NULL, 'PAGE:PROFILE', '2026-03-02 10:06:08'),
(327, 26, 'VISIT', NULL, 'PAGE:PROFILE', '2026-03-02 10:06:08'),
(328, 26, 'VISIT', NULL, 'PAGE:HOME', '2026-03-03 16:14:34'),
(329, 26, 'VISIT', NULL, 'PAGE:PROFILE', '2026-03-03 16:14:37'),
(330, 26, 'VISIT', NULL, 'PAGE:PROFILE', '2026-03-03 16:14:37'),
(331, 26, 'VISIT', NULL, 'PAGE:HOME', '2026-03-03 16:15:24'),
(332, 26, 'VISIT', NULL, 'PAGE:PROFILE', '2026-03-03 16:15:25'),
(333, 26, 'VISIT', NULL, 'PAGE:PROFILE', '2026-03-03 16:15:25'),
(334, 26, 'VISIT', NULL, 'PAGE:HOME', '2026-03-03 16:15:43'),
(335, 26, 'VISIT', NULL, 'PAGE:PROFILE', '2026-03-03 16:15:46'),
(336, 26, 'VISIT', NULL, 'PAGE:PROFILE', '2026-03-03 16:15:46'),
(337, 26, 'VISIT', NULL, 'PAGE:HOME', '2026-03-03 16:16:05'),
(338, 26, 'VISIT', NULL, 'PAGE:HOME', '2026-03-03 16:16:09'),
(339, 26, 'VISIT', NULL, 'PAGE:TRANSPORT', '2026-03-03 16:16:38'),
(340, 26, 'VISIT', NULL, 'PAGE:PROFILE', '2026-03-03 16:17:21'),
(341, 26, 'VISIT', NULL, 'PAGE:PROFILE', '2026-03-03 16:17:21'),
(342, 26, 'VISIT', NULL, 'PAGE:HOME', '2026-03-03 16:17:25'),
(343, 26, 'VISIT', NULL, 'PAGE:PROFILE', '2026-03-03 16:18:19'),
(344, 26, 'VISIT', NULL, 'PAGE:PROFILE', '2026-03-03 16:18:19'),
(345, 26, 'VISIT', NULL, 'PAGE:HOME', '2026-03-03 16:24:47'),
(346, 26, 'VISIT', NULL, 'PAGE:PROFILE', '2026-03-03 16:24:50'),
(347, 26, 'VISIT', NULL, 'PAGE:PROFILE', '2026-03-03 16:24:50'),
(348, 26, 'VISIT', NULL, 'PAGE:PROFILE', '2026-03-03 16:25:05'),
(349, 26, 'VISIT', NULL, 'PAGE:PROFILE', '2026-03-03 16:25:05'),
(350, 26, 'VISIT', NULL, 'PAGE:HOME', '2026-03-03 16:25:21'),
(351, 26, 'VISIT', NULL, 'PAGE:PROFILE', '2026-03-03 16:25:23'),
(352, 26, 'VISIT', NULL, 'PAGE:PROFILE', '2026-03-03 16:25:23'),
(353, 26, 'VISIT', NULL, 'PAGE:HOME', '2026-03-03 16:33:49');

--
-- Index pour les tables déchargées
--

--
-- Index pour la table `accommodation`
--
ALTER TABLE `accommodation`
  ADD PRIMARY KEY (`id`),
  ADD KEY `idx_accommodation_status` (`status`),
  ADD KEY `idx_accommodation_type` (`type`),
  ADD KEY `idx_accommodation_city` (`city`);

--
-- Index pour la table `activities`
--
ALTER TABLE `activities`
  ADD PRIMARY KEY (`activity_id`),
  ADD KEY `destination_id` (`destination_id`);

--
-- Index pour la table `bookingacc`
--
ALTER TABLE `bookingacc`
  ADD PRIMARY KEY (`id`),
  ADD KEY `idx_bookingacc_room_status_dates` (`room_id`,`status`,`check_in`,`check_out`),
  ADD KEY `idx_bookingacc_user_created` (`user_id`,`created_at`),
  ADD KEY `idx_bookingacc_status_created` (`status`,`created_at`);

--
-- Index pour la table `bookingdes`
--
ALTER TABLE `bookingdes`
  ADD PRIMARY KEY (`booking_id`),
  ADD KEY `user_id` (`user_id`),
  ADD KEY `destination_id` (`destination_id`),
  ADD KEY `activity_id` (`activity_id`);

--
-- Index pour la table `bookingtrans`
--
ALTER TABLE `bookingtrans`
  ADD PRIMARY KEY (`booking_id`),
  ADD KEY `user_id` (`user_id`),
  ADD KEY `transport_id` (`transport_id`),
  ADD KEY `schedule_id` (`schedule_id`);

--
-- Index pour la table `comments`
--
ALTER TABLE `comments`
  ADD PRIMARY KEY (`id`);

--
-- Index pour la table `destinations`
--
ALTER TABLE `destinations`
  ADD PRIMARY KEY (`destination_id`),
  ADD KEY `idx_destinations_country_city` (`country`,`city`),
  ADD KEY `idx_destinations_type` (`type`);

--
-- Index pour la table `destination_trans`
--
ALTER TABLE `destination_trans`
  ADD PRIMARY KEY (`destination_id`);

--
-- Index pour la table `loyalty_points`
--
ALTER TABLE `loyalty_points`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `user_id` (`user_id`);

--
-- Index pour la table `offers`
--
ALTER TABLE `offers`
  ADD PRIMARY KEY (`id_offer`),
  ADD KEY `pack_id` (`pack_id`),
  ADD KEY `destination_id` (`destination_id`),
  ADD KEY `accommodation_id` (`accommodation_id`);

--
-- Index pour la table `packs`
--
ALTER TABLE `packs`
  ADD PRIMARY KEY (`id_pack`),
  ADD KEY `destination_id` (`destination_id`),
  ADD KEY `accommodation_id` (`accommodation_id`),
  ADD KEY `activity_id` (`activity_id`),
  ADD KEY `transport_id` (`transport_id`),
  ADD KEY `category_id` (`category_id`);

--
-- Index pour la table `packs_bookings`
--
ALTER TABLE `packs_bookings`
  ADD PRIMARY KEY (`id_booking`),
  ADD KEY `idx_user_id` (`user_id`),
  ADD KEY `idx_pack_id` (`pack_id`),
  ADD KEY `idx_booking_date` (`booking_date`),
  ADD KEY `idx_status` (`status`);

--
-- Index pour la table `pack_categories`
--
ALTER TABLE `pack_categories`
  ADD PRIMARY KEY (`id_category`);

--
-- Index pour la table `posts`
--
ALTER TABLE `posts`
  ADD PRIMARY KEY (`id`);

--
-- Index pour la table `reactions`
--
ALTER TABLE `reactions`
  ADD PRIMARY KEY (`id`),
  ADD KEY `fk_reactions_story` (`travel_story_id`),
  ADD KEY `idx_reactions_post` (`post_id`),
  ADD KEY `idx_reactions_comment` (`comment_id`);

--
-- Index pour la table `reviews`
--
ALTER TABLE `reviews`
  ADD PRIMARY KEY (`review_id`),
  ADD KEY `idx_reviews_target` (`target_type`,`target_id`),
  ADD KEY `idx_reviews_user` (`user_id`);

--
-- Index pour la table `room`
--
ALTER TABLE `room`
  ADD PRIMARY KEY (`id`),
  ADD KEY `accommodation_id` (`accommodation_id`);

--
-- Index pour la table `room_images`
--
ALTER TABLE `room_images`
  ADD PRIMARY KEY (`id`),
  ADD KEY `idx_room_images_room_id` (`room_id`),
  ADD KEY `idx_room_images_room_order` (`room_id`,`display_order`),
  ADD KEY `idx_room_images_room_primary` (`room_id`,`is_primary`);

--
-- Index pour la table `schedule`
--
ALTER TABLE `schedule`
  ADD PRIMARY KEY (`schedule_id`),
  ADD KEY `fk_schedule_transport` (`transport_id`),
  ADD KEY `fk_schedule_departure` (`departure_destination_id`),
  ADD KEY `fk_schedule_arrival` (`arrival_destination_id`);

--
-- Index pour la table `shares`
--
ALTER TABLE `shares`
  ADD PRIMARY KEY (`id`),
  ADD KEY `idx_shares_post` (`post_id`),
  ADD KEY `idx_shares_story` (`travel_story_id`),
  ADD KEY `idx_shares_user` (`user_id`);

--
-- Index pour la table `stories`
--
ALTER TABLE `stories`
  ADD PRIMARY KEY (`id`),
  ADD KEY `idx_stories_user` (`user_id`),
  ADD KEY `idx_stories_exp` (`expires_at`);

--
-- Index pour la table `transport`
--
ALTER TABLE `transport`
  ADD PRIMARY KEY (`transport_id`);

--
-- Index pour la table `travel_story`
--
ALTER TABLE `travel_story`
  ADD PRIMARY KEY (`travel_story_id`),
  ADD KEY `user_id` (`user_id`);

--
-- Index pour la table `user`
--
ALTER TABLE `user`
  ADD PRIMARY KEY (`user_id`),
  ADD UNIQUE KEY `email` (`email`),
  ADD KEY `idx_email` (`email`),
  ADD KEY `idx_status` (`status`),
  ADD KEY `idx_role` (`role`);

--
-- Index pour la table `userpreferences`
--
ALTER TABLE `userpreferences`
  ADD PRIMARY KEY (`preference_id`),
  ADD UNIQUE KEY `user_id` (`user_id`),
  ADD KEY `idx_user_id` (`user_id`),
  ADD KEY `idx_budget_range` (`budget_min_per_night`,`budget_max_per_night`);

--
-- Index pour la table `user_activity_log`
--
ALTER TABLE `user_activity_log`
  ADD PRIMARY KEY (`log_id`),
  ADD KEY `user_id` (`user_id`);

--
-- AUTO_INCREMENT pour les tables déchargées
--

--
-- AUTO_INCREMENT pour la table `accommodation`
--
ALTER TABLE `accommodation`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=30;

--
-- AUTO_INCREMENT pour la table `activities`
--
ALTER TABLE `activities`
  MODIFY `activity_id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=28;

--
-- AUTO_INCREMENT pour la table `bookingacc`
--
ALTER TABLE `bookingacc`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=33;

--
-- AUTO_INCREMENT pour la table `bookingdes`
--
ALTER TABLE `bookingdes`
  MODIFY `booking_id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=20;

--
-- AUTO_INCREMENT pour la table `bookingtrans`
--
ALTER TABLE `bookingtrans`
  MODIFY `booking_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=104;

--
-- AUTO_INCREMENT pour la table `comments`
--
ALTER TABLE `comments`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=41;

--
-- AUTO_INCREMENT pour la table `destinations`
--
ALTER TABLE `destinations`
  MODIFY `destination_id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=130;

--
-- AUTO_INCREMENT pour la table `destination_trans`
--
ALTER TABLE `destination_trans`
  MODIFY `destination_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=9;

--
-- AUTO_INCREMENT pour la table `loyalty_points`
--
ALTER TABLE `loyalty_points`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=9;

--
-- AUTO_INCREMENT pour la table `offers`
--
ALTER TABLE `offers`
  MODIFY `id_offer` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- AUTO_INCREMENT pour la table `packs`
--
ALTER TABLE `packs`
  MODIFY `id_pack` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;

--
-- AUTO_INCREMENT pour la table `packs_bookings`
--
ALTER TABLE `packs_bookings`
  MODIFY `id_booking` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT pour la table `pack_categories`
--
ALTER TABLE `pack_categories`
  MODIFY `id_category` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=6;

--
-- AUTO_INCREMENT pour la table `posts`
--
ALTER TABLE `posts`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=20;

--
-- AUTO_INCREMENT pour la table `reactions`
--
ALTER TABLE `reactions`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=48;

--
-- AUTO_INCREMENT pour la table `reviews`
--
ALTER TABLE `reviews`
  MODIFY `review_id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT pour la table `room`
--
ALTER TABLE `room`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=31;

--
-- AUTO_INCREMENT pour la table `room_images`
--
ALTER TABLE `room_images`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=26;

--
-- AUTO_INCREMENT pour la table `schedule`
--
ALTER TABLE `schedule`
  MODIFY `schedule_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=59;

--
-- AUTO_INCREMENT pour la table `shares`
--
ALTER TABLE `shares`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=6;

--
-- AUTO_INCREMENT pour la table `stories`
--
ALTER TABLE `stories`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=11;

--
-- AUTO_INCREMENT pour la table `transport`
--
ALTER TABLE `transport`
  MODIFY `transport_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=45;

--
-- AUTO_INCREMENT pour la table `travel_story`
--
ALTER TABLE `travel_story`
  MODIFY `travel_story_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=28;

--
-- AUTO_INCREMENT pour la table `user`
--
ALTER TABLE `user`
  MODIFY `user_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=30;

--
-- AUTO_INCREMENT pour la table `userpreferences`
--
ALTER TABLE `userpreferences`
  MODIFY `preference_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=21;

--
-- AUTO_INCREMENT pour la table `user_activity_log`
--
ALTER TABLE `user_activity_log`
  MODIFY `log_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=354;

--
-- Contraintes pour les tables déchargées
--

--
-- Contraintes pour la table `activities`
--
ALTER TABLE `activities`
  ADD CONSTRAINT `activities_ibfk_1` FOREIGN KEY (`destination_id`) REFERENCES `destinations` (`destination_id`) ON DELETE CASCADE;

--
-- Contraintes pour la table `bookingacc`
--
ALTER TABLE `bookingacc`
  ADD CONSTRAINT `fk_bookingacc_room` FOREIGN KEY (`room_id`) REFERENCES `room` (`id`) ON DELETE CASCADE,
  ADD CONSTRAINT `fk_bookingacc_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`) ON DELETE CASCADE;

--
-- Contraintes pour la table `bookingtrans`
--
ALTER TABLE `bookingtrans`
  ADD CONSTRAINT `bookingtrans_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`) ON DELETE CASCADE,
  ADD CONSTRAINT `bookingtrans_ibfk_2` FOREIGN KEY (`transport_id`) REFERENCES `transport` (`transport_id`) ON DELETE CASCADE,
  ADD CONSTRAINT `bookingtrans_ibfk_3` FOREIGN KEY (`schedule_id`) REFERENCES `schedule` (`schedule_id`) ON DELETE CASCADE;

--
-- Contraintes pour la table `loyalty_points`
--
ALTER TABLE `loyalty_points`
  ADD CONSTRAINT `loyalty_points_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`) ON DELETE CASCADE;

--
-- Contraintes pour la table `packs_bookings`
--
ALTER TABLE `packs_bookings`
  ADD CONSTRAINT `packs_bookings_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`) ON DELETE CASCADE,
  ADD CONSTRAINT `packs_bookings_ibfk_2` FOREIGN KEY (`pack_id`) REFERENCES `packs` (`id_pack`) ON DELETE CASCADE;

--
-- Contraintes pour la table `reactions`
--
ALTER TABLE `reactions`
  ADD CONSTRAINT `fk_reactions_comment` FOREIGN KEY (`comment_id`) REFERENCES `comments` (`id`) ON DELETE CASCADE,
  ADD CONSTRAINT `fk_reactions_post` FOREIGN KEY (`post_id`) REFERENCES `posts` (`id`) ON DELETE CASCADE,
  ADD CONSTRAINT `fk_reactions_story` FOREIGN KEY (`travel_story_id`) REFERENCES `travel_story` (`travel_story_id`) ON DELETE CASCADE;

--
-- Contraintes pour la table `reviews`
--
ALTER TABLE `reviews`
  ADD CONSTRAINT `reviews_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`) ON DELETE CASCADE;

--
-- Contraintes pour la table `room`
--
ALTER TABLE `room`
  ADD CONSTRAINT `room_ibfk_1` FOREIGN KEY (`accommodation_id`) REFERENCES `accommodation` (`id`) ON DELETE CASCADE;

--
-- Contraintes pour la table `room_images`
--
ALTER TABLE `room_images`
  ADD CONSTRAINT `fk_room_images_room` FOREIGN KEY (`room_id`) REFERENCES `room` (`id`) ON DELETE CASCADE;

--
-- Contraintes pour la table `schedule`
--
ALTER TABLE `schedule`
  ADD CONSTRAINT `fk_schedule_arrival` FOREIGN KEY (`arrival_destination_id`) REFERENCES `destination_trans` (`destination_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `fk_schedule_arrival_destination` FOREIGN KEY (`arrival_destination_id`) REFERENCES `destination_trans` (`destination_id`) ON UPDATE CASCADE,
  ADD CONSTRAINT `fk_schedule_departure` FOREIGN KEY (`departure_destination_id`) REFERENCES `destination_trans` (`destination_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `fk_schedule_departure_destination` FOREIGN KEY (`departure_destination_id`) REFERENCES `destination_trans` (`destination_id`) ON UPDATE CASCADE,
  ADD CONSTRAINT `fk_schedule_transport` FOREIGN KEY (`transport_id`) REFERENCES `transport` (`transport_id`) ON DELETE CASCADE;

--
-- Contraintes pour la table `shares`
--
ALTER TABLE `shares`
  ADD CONSTRAINT `shares_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`) ON DELETE CASCADE,
  ADD CONSTRAINT `shares_ibfk_2` FOREIGN KEY (`post_id`) REFERENCES `posts` (`id`) ON DELETE CASCADE,
  ADD CONSTRAINT `shares_ibfk_3` FOREIGN KEY (`travel_story_id`) REFERENCES `travel_story` (`travel_story_id`) ON DELETE CASCADE;

--
-- Contraintes pour la table `travel_story`
--
ALTER TABLE `travel_story`
  ADD CONSTRAINT `travel_story_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`) ON DELETE CASCADE;

--
-- Contraintes pour la table `userpreferences`
--
ALTER TABLE `userpreferences`
  ADD CONSTRAINT `fk_preferences_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`) ON DELETE CASCADE,
  ADD CONSTRAINT `userpreferences_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`) ON DELETE CASCADE;

--
-- Contraintes pour la table `user_activity_log`
--
ALTER TABLE `user_activity_log`
  ADD CONSTRAINT `user_activity_log_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`) ON DELETE CASCADE;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
