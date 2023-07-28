-- Table structure for table "Employee"

-- Table structure for table "address"

CREATE TABLE IF NOT EXISTS "address" (
  "id" BIGSERIAL PRIMARY KEY,
  "city" VARCHAR(255),
  "country" VARCHAR(255),
  "street" VARCHAR(255),
  "created_on" VARCHAR(255),
  "modified_on" VARCHAR(255),
  "user_id" BIGINT,
  CONSTRAINT "UK_7rod8a71yep5vxasb0ms3osbg" UNIQUE ("user_id")
);

-- Table structure for table "auth"

CREATE TABLE IF NOT EXISTS "auth" (
  "id" BIGSERIAL PRIMARY KEY,
  "created_on" VARCHAR(255),
  "modified_on" VARCHAR(255),
  "password" VARCHAR(255),
  "username" VARCHAR(255),
  "user_id" BIGINT,
  CONSTRAINT "UK_ox9lr2lxr8h7undhmflx4xqky" UNIQUE ("user_id")
);

-- Table structure for table "info"
CREATE TABLE IF NOT EXISTS "info" (
  "id" BIGSERIAL PRIMARY KEY,
  "email" VARCHAR(255),
  "name" VARCHAR(255),
  "created_on" VARCHAR(255),
  "modified_on" VARCHAR(255),
  "lastname" VARCHAR(255),
  "role" VARCHAR(255),
  "img_url" VARCHAR(255),
  CONSTRAINT "unique_email" UNIQUE ("email"),
  CONSTRAINT "unique_name" UNIQUE ("name")
);
