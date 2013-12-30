alter table dpt_departements drop constraint enforce_srid_geometrie;
update dpt_departements set geometrie = st_transform(geometrie,4326);