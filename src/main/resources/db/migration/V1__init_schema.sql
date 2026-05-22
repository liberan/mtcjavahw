CREATE TABLE tasks (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(100) NOT NULL,
    description VARCHAR(500),
    completed BOOLEAN NOT NULL DEFAULT FALSE,
    due_date DATE,
    priority VARCHAR(16),
    tags VARCHAR(500),
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);

CREATE TABLE task_attachments (
    id BIGSERIAL PRIMARY KEY,
    task_id BIGINT NOT NULL,
    file_name VARCHAR(255) NOT NULL,
    stored_file_name VARCHAR(255) NOT NULL,
    content_type VARCHAR(255),
    size BIGINT NOT NULL,
    uploaded_at TIMESTAMP,
    CONSTRAINT fk_task_attachments_task FOREIGN KEY (task_id)
        REFERENCES tasks (id) ON DELETE CASCADE
);

CREATE INDEX idx_task_attachments_task_id ON task_attachments (task_id);
CREATE INDEX idx_tasks_due_date ON tasks (due_date);
CREATE INDEX idx_tasks_priority ON tasks (priority);
