Migration from username-in-rows to users table

If you have existing data in tables `notes` and `reminders` that store `user_name`,
use the following MySQL steps to migrate without losing data. Always back up first.

1) Create new `users` table and add `user_id` columns to each table

```sql
-- Create users table
CREATE TABLE users (
  id BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  user_name VARCHAR(191) NOT NULL,
  password VARCHAR(255) NOT NULL DEFAULT '$2a$10$Q8l7wM7b7hF0G9dQyZs5Ue0pQ9kZr/placeholder',
  display_name VARCHAR(191) NULL,
  email VARCHAR(191) NULL,
  created_at DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
  CONSTRAINT pk_users PRIMARY KEY (id),
  CONSTRAINT uq_users_user_name UNIQUE (user_name)
);

-- Add user_id columns
ALTER TABLE notes ADD COLUMN user_id BIGINT UNSIGNED NULL;
ALTER TABLE reminders ADD COLUMN user_id BIGINT UNSIGNED NULL;
ALTER TABLE todos ADD COLUMN user_id BIGINT UNSIGNED NULL;
```

2) Populate `users` from distinct user names and backfill `user_id`

```sql
-- Insert distinct users from notes and reminders
INSERT IGNORE INTO users(user_name, password)
SELECT DISTINCT n.user_name, '$2a$10$Q8l7wM7b7hF0G9dQyZs5Ue0pQ9kZr/placeholder' FROM notes n
UNION
SELECT DISTINCT r.user_name, '$2a$10$Q8l7wM7b7hF0G9dQyZs5Ue0pQ9kZr/placeholder' FROM reminders r;

-- Backfill user_id via username
UPDATE notes n JOIN users u ON n.user_name = u.user_name SET n.user_id = u.id;
UPDATE reminders r JOIN users u ON r.user_name = u.user_name SET r.user_id = u.id;

-- If todos are global, decide an owner strategy (e.g., assign to an admin user)
-- UPDATE todos SET user_id = <some_user_id>;
```

3) Enforce NOT NULL + FKs and drop old columns

```sql
ALTER TABLE notes MODIFY user_id BIGINT UNSIGNED NOT NULL, ADD CONSTRAINT fk_notes_user FOREIGN KEY(user_id) REFERENCES users(id) ON DELETE CASCADE;
ALTER TABLE reminders MODIFY user_id BIGINT UNSIGNED NOT NULL, ADD CONSTRAINT fk_reminders_user FOREIGN KEY(user_id) REFERENCES users(id) ON DELETE CASCADE;
ALTER TABLE todos MODIFY user_id BIGINT UNSIGNED NOT NULL, ADD CONSTRAINT fk_todos_user FOREIGN KEY(user_id) REFERENCES users(id) ON DELETE CASCADE;

ALTER TABLE notes DROP COLUMN user_name;
ALTER TABLE reminders DROP COLUMN user_name;
```

4) Update application to new API and add JWT (`Authorization: Bearer <token>`) header.

Notes
- Existing passwords unknown -> placeholders above; require users to reset password.
- Alternatively, create accounts via the new `/api/v1/auth/register` endpoint, then map data.
