--
-- J. Moquet 29/10/2008
-- Permet de changer les propriétaires des tables de JDONREF (ainsi que la database).
-- compatible JDONREF v2.1
--

-- les tables jdonref
ALTER TABLE poizon OWNER TO jdonrefadmin;

GRANT SELECT ON poizon TO jdonrefadmin;
GRANT INSERT ON poizon TO jdonrefadmin;
GRANT UPDATE ON poizon TO jdonrefadmin;
GRANT DELETE ON poizon TO jdonrefadmin;

GRANT SELECT ON poizon TO jdonref;