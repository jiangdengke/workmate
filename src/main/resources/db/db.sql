

-- todos表
CREATE TABLE IF NOT EXISTS todos (
                                     id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
                                     title TEXT NOT NULL COMMENT '待办标题',
                                     done BOOLEAN NOT NULL DEFAULT FALSE COMMENT '是否完成',
                                     created_at DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
                                     done_at DATETIME(3),
                                     CONSTRAINT pk_todos PRIMARY KEY (id),
                                     CONSTRAINT chk_todos_title_not_blank CHECK (LENGTH(TRIM(title)) > 0),
                                     CONSTRAINT chk_todos_done_time CHECK (done_at IS NULL OR done_at >= created_at)
);

CREATE INDEX idx_todos_created_at ON todos (created_at);
CREATE INDEX idx_todos_done ON todos (done);
CREATE INDEX idx_todos_done_at ON todos (done_at);

-- notes表
CREATE TABLE IF NOT EXISTS notes (
                                     id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
                                     user_name VARCHAR(191) NOT NULL COMMENT '用户名（简单字符串，未做外键约束）',
                                     text TEXT NOT NULL,
                                     created_at DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
                                     CONSTRAINT pk_notes PRIMARY KEY (id),
                                     CONSTRAINT chk_notes_user_not_blank CHECK (LENGTH(TRIM(user_name)) > 0),
                                     CONSTRAINT chk_notes_text_not_blank CHECK (LENGTH(TRIM(text)) > 0)
);

CREATE INDEX idx_notes_user_created_at ON notes (user_name, created_at);

-- reminders表
CREATE TABLE IF NOT EXISTS reminders (
                                         id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
                                         user_name VARCHAR(191) NOT NULL,
                                         text TEXT NOT NULL,
                                         at_time DATETIME(3) NOT NULL COMMENT '触发时间',
                                         sent BOOLEAN NOT NULL DEFAULT FALSE COMMENT '是否已触发',
                                         created_at DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
                                         CONSTRAINT pk_reminders PRIMARY KEY (id),
                                         CONSTRAINT chk_reminders_user_not_blank CHECK (LENGTH(TRIM(user_name)) > 0),
                                         CONSTRAINT chk_reminders_text_not_blank CHECK (LENGTH(TRIM(text)) > 0)
);

CREATE INDEX idx_reminders_sent_at ON reminders (sent, at_time);
CREATE INDEX idx_reminders_at_time ON reminders (at_time);
