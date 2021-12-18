package com.example.auth.dao.repository;

import java.util.UUID;

import org.springframework.data.r2dbc.repository.R2dbcRepository;

import com.example.auth.dao.model.Session;

public interface SessionRepository extends R2dbcRepository<Session, UUID> {

}
