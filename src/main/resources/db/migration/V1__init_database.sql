-- Department Table
CREATE TABLE department (
                            id SERIAL PRIMARY KEY,
                            name VARCHAR(255) NOT NULL,
                            description TEXT,
                            created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                            updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Employee Table
CREATE TABLE employee (
                          id SERIAL PRIMARY KEY,
                          first_name VARCHAR(255) NOT NULL,
                          last_name VARCHAR(255) NOT NULL,
                          phone VARCHAR(255) UNIQUE NOT NULL,  -- Equivalent to @Column(unique = true)
                          address TEXT,
                          email VARCHAR(255) UNIQUE NOT NULL,  -- Equivalent to @Column(unique = true)
                          status BOOLEAN DEFAULT FALSE,  -- Equivalent to @Builder.Default private Boolean status = false
                          department_id BIGINT,  -- Foreign key reference to Department
                          created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,  -- Equivalent to @CreatedDate
                          updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,  -- Equivalent to @LastModifiedDate
                          CONSTRAINT fk_department
                              FOREIGN KEY(department_id)
                                  REFERENCES department(id)  -- Maps to @ManyToOne @JoinColumn(name = "department_id")
                                  ON DELETE SET NULL
);

-- Create Roles table
CREATE TABLE roles (
                       id SERIAL PRIMARY KEY,
                       name VARCHAR(255) UNIQUE NOT NULL,
                       description TEXT,
                       employee_id BIGINT,
                       created_at TIMESTAMP,
                       updated_at TIMESTAMP,
                       CONSTRAINT fk_employee
                           FOREIGN KEY(employee_id)
                               REFERENCES employee(id)
                               ON DELETE SET NULL
);


CREATE INDEX idx_employee_email ON employee(email);
CREATE INDEX idx_department_name ON department(name);
CREATE INDEX idx_roles_name ON roles(name);

CREATE SEQUENCE employee_sequence START WITH 1 INCREMENT BY 1 NO MINVALUE NO MAXVALUE CACHE 1;
CREATE SEQUENCE department_sequence START WITH 1 INCREMENT BY 1 NO MINVALUE NO MAXVALUE CACHE 1;
CREATE SEQUENCE role_sequence START WITH 1 INCREMENT BY 1 NO MINVALUE NO MAXVALUE CACHE 1;
