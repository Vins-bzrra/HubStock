package com.vins.hubstock.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "itemChange")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ItemChange {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String patrimonyItem;

    @Column(nullable = false)
    private String attributeName;

    @Column(nullable = false)
    private String oldValue;

    @Column(nullable = false)
    private String newValue;

    @Column(nullable = false)
    private String userRegistration;

    @Column(nullable = false)
    private String UserName;

    @Column(nullable = false)
    private LocalDateTime changeDateTime;

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ItemChange other = (ItemChange) obj;
        return Objects.equals(id, other.id);
    }
}
