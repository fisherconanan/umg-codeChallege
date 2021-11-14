package com.umg.isrcapp;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.Objects;

@Entity
public class SpotifyMetadata {
    private @Id
    @GeneratedValue
    Long id;
    private String name;

    private String ISRC;
    private int duration_ms;
    private boolean explicit;

    public SpotifyMetadata() { this.name = "test";}

    SpotifyMetadata(String name, String ISRC, Integer duration_ms,  Boolean explicit) {
        this.name = name;
        this.ISRC = ISRC;
        this.duration_ms = duration_ms;
        this.explicit = explicit;
    }

    public Long getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public String getISRC() { return ISRC; }

    public void setISRC(String ISRC) { this.ISRC = ISRC; }

    public int getDuration_ms() { return duration_ms; }

    public void setDuration_ms(int duration_ms) { this.duration_ms = duration_ms; }

    public boolean isExplicit() { return explicit; }

    public void setExplicit(boolean explicit) { this.explicit = explicit; }

    public void setId(Long id) { this.id = id; }

    public void setName(String name) { this.name = name; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SpotifyMetadata that = (SpotifyMetadata) o;
        return getDuration_ms() == that.getDuration_ms() && isExplicit() == that.isExplicit() && getId().equals(that.getId()) && Objects.equals(getName(), that.getName()) && Objects.equals(getISRC(), that.getISRC());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getName(), getISRC(), getDuration_ms(), isExplicit());
    }

    @Override
    public String toString() {
        return "SpotifyMetadata{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", ISRC='" + ISRC + '\'' +
                ", duration_ms=" + duration_ms +
                ", explicit=" + explicit +
                '}';
    }
}
