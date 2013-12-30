alter table com_communes drop constraint enforce_srid_geometrie;
update com_communes set geometrie = st_transform(geometrie,4326);