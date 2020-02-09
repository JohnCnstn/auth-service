------------------------------------------------------------------------------------------------------------------------
--- EXTENSIONS
------------------------------------------------------------------------------------------------------------------------
CREATE
EXTENSION IF NOT EXISTS "uuid-ossp";
CREATE
EXTENSION IF NOT EXISTS "citext";
CREATE
EXTENSION IF NOT EXISTS "fuzzystrmatch";
CREATE
EXTENSION IF NOT EXISTS "pg_trgm";

------------------------------------------------------------------------------------------------------------------------
--- TYPES
------------------------------------------------------------------------------------------------------------------------
CREATE
TYPE USER_ROLE AS ENUM ('USER', 'ADMIN');

------------------------------------------------------------------------------------------------------------------------
--- TABLES
------------------------------------------------------------------------------------------------------------------------
CREATE TABLE users
(
  id UUID DEFAULT uuid_generate_v4() NOT NULL PRIMARY KEY,

  email         TEXT NOT NULL UNIQUE,
  password_hash TEXT NOT NULL,
  role USER_ROLE NOT NULL,

  created_at    TIMESTAMP WITH TIME ZONE DEFAULT now() NOT NULL,
  updated_at    TIMESTAMP WITH TIME ZONE NULL,
  deleted_at    TIMESTAMP WITH TIME ZONE NULL
);
