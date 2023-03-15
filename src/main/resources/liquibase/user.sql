-- liquibase formatted sql

--changeset akulov:1
CREATE TABLE notification_tasks (
                                    id BIGSERIAL PRIMARY KEY,
                                    user_id BIGINT,
                                    "message" TEXT,
                                    "date" TIMESTAMP