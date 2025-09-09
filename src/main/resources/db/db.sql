-- users
CREATE TABLE IF NOT EXISTS users (
  id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  user_name     VARCHAR(191) NOT NULL COMMENT '登录用户名',
  password      VARCHAR(255) NOT NULL COMMENT 'BCrypt 密码哈希',
  display_name VARCHAR(191) NULL COMMENT '展示名',
  email VARCHAR(191) NULL,
  created_at DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
  CONSTRAINT pk_users PRIMARY KEY (id),
  CONSTRAINT uq_users_user_name UNIQUE (user_name),
  CONSTRAINT uq_users_email UNIQUE (email),
  CONSTRAINT chk_users_user_not_blank CHECK (LENGTH(TRIM(user_name)) > 0)
);

CREATE INDEX idx_users_created_at ON users (created_at);

-- todos
CREATE TABLE IF NOT EXISTS todos (
  id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  user_id BIGINT UNSIGNED NOT NULL COMMENT '用户ID',
  title TEXT NOT NULL COMMENT '待办标题',
  done BOOLEAN NOT NULL DEFAULT FALSE COMMENT '是否完成',
  created_at DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
  done_at DATETIME(3),
  CONSTRAINT pk_todos PRIMARY KEY (id),
  CONSTRAINT fk_todos_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
  CONSTRAINT chk_todos_title_not_blank CHECK (LENGTH(TRIM(title)) > 0),
  CONSTRAINT chk_todos_done_time CHECK (done_at IS NULL OR done_at >= created_at)
);

CREATE INDEX idx_todos_created_at ON todos (created_at);
CREATE INDEX idx_todos_user_created_at ON todos (user_id, created_at);
CREATE INDEX idx_todos_done ON todos (done);
CREATE INDEX idx_todos_done_at ON todos (done_at);

-- notes
CREATE TABLE IF NOT EXISTS notes (
  id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  user_id BIGINT UNSIGNED NOT NULL COMMENT '用户ID',
  text TEXT NOT NULL,
  created_at DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
  CONSTRAINT pk_notes PRIMARY KEY (id),
  CONSTRAINT fk_notes_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
  CONSTRAINT chk_notes_text_not_blank CHECK (LENGTH(TRIM(text)) > 0)
);

CREATE INDEX idx_notes_user_created_at ON notes (user_id, created_at);

-- reminders
CREATE TABLE IF NOT EXISTS reminders (
  id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  user_id BIGINT UNSIGNED NOT NULL,
  text TEXT NOT NULL,
  at_time DATETIME(3) NOT NULL COMMENT '触发时间',
  sent BOOLEAN NOT NULL DEFAULT FALSE COMMENT '是否已触发',
  created_at DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
  CONSTRAINT pk_reminders PRIMARY KEY (id),
  CONSTRAINT fk_reminders_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
  CONSTRAINT chk_reminders_text_not_blank CHECK (LENGTH(TRIM(text)) > 0)
);

CREATE INDEX idx_reminders_sent_at ON reminders (sent, at_time);
CREATE INDEX idx_reminders_at_time ON reminders (at_time);
CREATE INDEX idx_reminders_user_at ON reminders (user_id, at_time);

-- email verification codes (unified purpose)
CREATE TABLE IF NOT EXISTS email_codes (
  id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  email VARCHAR(191) NOT NULL,
  code VARCHAR(32) NOT NULL,
  expire_at DATETIME(3) NOT NULL,
  last_send_at DATETIME(3) NOT NULL,
  created_at DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
  CONSTRAINT pk_email_codes PRIMARY KEY (id),
  CONSTRAINT uq_email_codes_email UNIQUE (email),
  CONSTRAINT chk_email_codes_code_not_blank CHECK (LENGTH(TRIM(code)) > 0)
);

CREATE INDEX idx_email_codes_expire ON email_codes (expire_at);
