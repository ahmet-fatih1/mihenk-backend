-- =====================================================
-- V1: Temel yapı — tenancy, kullanıcı/yetki, müşteri
-- =====================================================

CREATE EXTENSION IF NOT EXISTS btree_gist;

-- ============ TENANCY ============

CREATE TABLE company (
    id              bigserial PRIMARY KEY,
    name            varchar(200) NOT NULL,
    type            varchar(20)  NOT NULL,   -- INDEPENDENT | CORPORATE
    tax_number      varchar(20),
    email           varchar(150),
    phone           varchar(30),
    status          varchar(20)  NOT NULL DEFAULT 'TRIAL',
                                             -- TRIAL | ACTIVE | SUSPENDED | CANCELLED
    created_at      timestamptz  NOT NULL DEFAULT now(),
    created_by      bigint,
    updated_at      timestamptz,
    updated_by      bigint,
    deleted_at      timestamptz,
    version         bigint       NOT NULL DEFAULT 0
);

CREATE UNIQUE INDEX uq_company_tax ON company (tax_number) WHERE deleted_at IS NULL;

CREATE TABLE branch (
    id              bigserial PRIMARY KEY,
    company_id      bigint       NOT NULL REFERENCES company(id),
    name            varchar(200) NOT NULL,
    code            varchar(30),
    city            varchar(100),
    address         text,
    phone           varchar(30),
    is_active       boolean      NOT NULL DEFAULT true,
    created_at      timestamptz  NOT NULL DEFAULT now(),
    created_by      bigint,
    updated_at      timestamptz,
    updated_by      bigint,
    deleted_at      timestamptz,
    version         bigint       NOT NULL DEFAULT 0
);

CREATE INDEX idx_branch_company ON branch (company_id) WHERE deleted_at IS NULL;

-- ============ YETKİ ============

CREATE TABLE permission (
    id          bigserial PRIMARY KEY,
    code        varchar(60) NOT NULL UNIQUE,   -- vehicle:write, report:read
    description varchar(200)
);

CREATE TABLE role (
    id          bigserial PRIMARY KEY,
    company_id  bigint,                        -- NULL = sistem rolü
    code        varchar(60)  NOT NULL,         -- COMPANY_OWNER, BRANCH_MANAGER...
    name        varchar(100) NOT NULL,
    is_system   boolean      NOT NULL DEFAULT false,
    created_at  timestamptz  NOT NULL DEFAULT now()
);

CREATE UNIQUE INDEX uq_role_company_code ON role (COALESCE(company_id, 0), code);

CREATE TABLE role_permission (
    role_id       bigint NOT NULL REFERENCES role(id) ON DELETE CASCADE,
    permission_id bigint NOT NULL REFERENCES permission(id) ON DELETE CASCADE,
    PRIMARY KEY (role_id, permission_id)
);

CREATE TABLE app_user (
    id              bigserial PRIMARY KEY,
    company_id      bigint       REFERENCES company(id),  -- NULL = SUPER_ADMIN
    branch_id       bigint       REFERENCES branch(id),   -- NULL = şirket geneli
    email           varchar(150) NOT NULL,
    password_hash   varchar(200) NOT NULL,
    first_name      varchar(100) NOT NULL,
    last_name       varchar(100) NOT NULL,
    phone           varchar(30),
    is_active       boolean      NOT NULL DEFAULT true,
    last_login_at   timestamptz,
    created_at      timestamptz  NOT NULL DEFAULT now(),
    created_by      bigint,
    updated_at      timestamptz,
    updated_by      bigint,
    deleted_at      timestamptz,
    version         bigint       NOT NULL DEFAULT 0
);

CREATE UNIQUE INDEX uq_user_email ON app_user (lower(email)) WHERE deleted_at IS NULL;
CREATE INDEX idx_user_company ON app_user (company_id) WHERE deleted_at IS NULL;

CREATE TABLE user_role (
    user_id bigint NOT NULL REFERENCES app_user(id) ON DELETE CASCADE,
    role_id bigint NOT NULL REFERENCES role(id) ON DELETE CASCADE,
    PRIMARY KEY (user_id, role_id)
);

CREATE TABLE refresh_token (
    id          bigserial PRIMARY KEY,
    user_id     bigint       NOT NULL REFERENCES app_user(id) ON DELETE CASCADE,
    token_hash  varchar(200) NOT NULL UNIQUE,
    expires_at  timestamptz  NOT NULL,
    revoked_at  timestamptz,
    created_at  timestamptz  NOT NULL DEFAULT now()
);

-- ============ MÜŞTERİ ============

CREATE TABLE customer (
    id                  bigserial PRIMARY KEY,
    company_id          bigint       NOT NULL REFERENCES company(id),
    type                varchar(20)  NOT NULL DEFAULT 'INDIVIDUAL',
                                                  -- INDIVIDUAL | CORPORATE
    first_name          varchar(100),
    last_name           varchar(100),
    company_name        varchar(200),             -- kurumsal müşteri
    national_id         varchar(20),              -- TCKN (şifreli tutulacak)
    tax_number          varchar(20),
    email               varchar(150),
    phone               varchar(30)  NOT NULL,
    birth_date          date,
    license_no          varchar(30),
    license_class       varchar(10),
    license_issue_date  date,
    address             text,
    city                varchar(100),
    notes               text,
    is_blacklisted      boolean      NOT NULL DEFAULT false,
    blacklist_reason    varchar(300),
    created_at          timestamptz  NOT NULL DEFAULT now(),
    created_by          bigint,
    updated_at          timestamptz,
    updated_by          bigint,
    deleted_at          timestamptz,
    version             bigint       NOT NULL DEFAULT 0
);

CREATE INDEX idx_customer_company  ON customer (company_id) WHERE deleted_at IS NULL;
CREATE INDEX idx_customer_phone    ON customer (company_id, phone) WHERE deleted_at IS NULL;
CREATE INDEX idx_customer_fullname ON customer (company_id, last_name, first_name) WHERE deleted_at IS NULL;
