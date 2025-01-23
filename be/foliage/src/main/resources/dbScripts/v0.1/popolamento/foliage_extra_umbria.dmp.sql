--
-- PostgreSQL database dump
--

-- Dumped from database version 13.13
-- Dumped by pg_dump version 14.2

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

--
-- Name: foliage_extra; Type: SCHEMA; Schema: -; Owner: foliage
--

CREATE SCHEMA foliage_extra;


ALTER SCHEMA foliage_extra OWNER TO foliage;

SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- Name: acque_pubbliche_503; Type: TABLE; Schema: foliage_extra; Owner: foliage
--

CREATE TABLE foliage_extra.acque_pubbliche_503 (
    id integer NOT NULL,
    geom public.geometry(MultiLineString,25833),
    fid_1 integer,
    fid integer,
    "OBJECTID" bigint,
    "SHAPE_LENG" double precision,
    "VERIFICA" character varying
);


ALTER TABLE foliage_extra.acque_pubbliche_503 OWNER TO foliage;

--
-- Name: acque_pubbliche_503_id_seq; Type: SEQUENCE; Schema: foliage_extra; Owner: foliage
--

CREATE SEQUENCE foliage_extra.acque_pubbliche_503_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE foliage_extra.acque_pubbliche_503_id_seq OWNER TO foliage;

--
-- Name: acque_pubbliche_503_id_seq; Type: SEQUENCE OWNED BY; Schema: foliage_extra; Owner: foliage
--

ALTER SEQUENCE foliage_extra.acque_pubbliche_503_id_seq OWNED BY foliage_extra.acque_pubbliche_503.id;


--
-- Name: acque_pubbliche_rispetto_504; Type: TABLE; Schema: foliage_extra; Owner: foliage
--

CREATE TABLE foliage_extra.acque_pubbliche_rispetto_504 (
    id integer NOT NULL,
    geom public.geometry(MultiPolygon,25833),
    fid integer,
    gid bigint,
    objectid bigint,
    id_rl_ character varying,
    rif_leg character varying,
    nome_gu character varying,
    num_gu bigint,
    id_gu character varying,
    data_gu timestamp without time zone,
    note_ character varying,
    atti character varying,
    comuni character varying,
    link_sira bigint,
    link_vt character varying,
    art23 character varying,
    allegati character varying,
    pr character varying,
    shape_area double precision,
    shape_len double precision,
    esclusione character varying,
    foce_sbocc character varying,
    limiti character varying,
    comuni_gu character varying,
    lim_prec character varying,
    nome_dive character varying,
    modifiche character varying
);


ALTER TABLE foliage_extra.acque_pubbliche_rispetto_504 OWNER TO foliage;

--
-- Name: altimetria_1200_505; Type: TABLE; Schema: foliage_extra; Owner: foliage
--

CREATE TABLE foliage_extra.altimetria_1200_505 (
    id integer NOT NULL,
    geom public.geometry(MultiPolygon,25833),
    fid integer,
    "CODVINCOLO" double precision,
    "NOME" character varying,
    "ANNOTAZION" character varying,
    "ID_RL" character varying,
    "ATTI" character varying,
    "ALEEGATI" character varying,
    "ALLEGATI" character varying,
    "SHAPE_AREA" double precision,
    "SHAPE_LEN" double precision
);


ALTER TABLE foliage_extra.altimetria_1200_505 OWNER TO foliage;

--
-- Name: altimetria_1200_505_id_seq; Type: SEQUENCE; Schema: foliage_extra; Owner: foliage
--

CREATE SEQUENCE foliage_extra.altimetria_1200_505_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE foliage_extra.altimetria_1200_505_id_seq OWNER TO foliage;

--
-- Name: altimetria_1200_505_id_seq; Type: SEQUENCE OWNED BY; Schema: foliage_extra; Owner: foliage
--

ALTER SEQUENCE foliage_extra.altimetria_1200_505_id_seq OWNED BY foliage_extra.altimetria_1200_505.id;


--
-- Name: aree_paesaggistiche_215; Type: TABLE; Schema: foliage_extra; Owner: foliage
--

CREATE TABLE foliage_extra.aree_paesaggistiche_215 (
    id integer NOT NULL,
    geom public.geometry(MultiPolygonZ,3004),
    comune character varying(49),
    localita character varying(120),
    let42_02 character varying(9),
    atto character varying(120),
    pubblicazi character varying(120),
    num_vinc character varying(50),
    art42_02 integer
);


ALTER TABLE foliage_extra.aree_paesaggistiche_215 OWNER TO foliage;

--
-- Name: aree_paesaggistiche_215_id_seq; Type: SEQUENCE; Schema: foliage_extra; Owner: foliage
--

CREATE SEQUENCE foliage_extra.aree_paesaggistiche_215_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE foliage_extra.aree_paesaggistiche_215_id_seq OWNER TO foliage;

--
-- Name: aree_paesaggistiche_215_id_seq; Type: SEQUENCE OWNED BY; Schema: foliage_extra; Owner: foliage
--

ALTER SEQUENCE foliage_extra.aree_paesaggistiche_215_id_seq OWNED BY foliage_extra.aree_paesaggistiche_215.id;


--
-- Name: aree_protette_106; Type: TABLE; Schema: foliage_extra; Owner: foliage
--

CREATE TABLE foliage_extra.aree_protette_106 (
    id character varying,
    geom public.geometry(MultiPolygon,4326),
    objectid_1 character varying,
    codice_are character varying,
    tipo character varying,
    nome_gazze character varying,
    ente_gesto character varying,
    provvedime character varying,
    superficie character varying,
    superfic_1 character varying,
    area_ha character varying,
    perimetro character varying,
    naz_reg character varying,
    shape_leng character varying
);


ALTER TABLE foliage_extra.aree_protette_106 OWNER TO foliage;

--
-- Name: boschi_517; Type: TABLE; Schema: foliage_extra; Owner: foliage
--

CREATE TABLE foliage_extra.boschi_517 (
    id integer NOT NULL,
    geom public.geometry(MultiPolygon,25833),
    fid integer,
    "GID" double precision,
    "CODVINCOLO" double precision,
    "ANNOTAZION" character varying,
    "ALLEGATI" character varying,
    "SHAPE_AREA" double precision,
    "SHAPE_LEN" double precision,
    "LAYER" character varying,
    "PATH" character varying
);


ALTER TABLE foliage_extra.boschi_517 OWNER TO foliage;

--
-- Name: boschi_517_id_seq; Type: SEQUENCE; Schema: foliage_extra; Owner: foliage
--

CREATE SEQUENCE foliage_extra.boschi_517_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE foliage_extra.boschi_517_id_seq OWNER TO foliage;

--
-- Name: boschi_517_id_seq; Type: SEQUENCE OWNED BY; Schema: foliage_extra; Owner: foliage
--

ALTER SEQUENCE foliage_extra.boschi_517_id_seq OWNED BY foliage_extra.boschi_517.id;


--
-- Name: costa_laghi_502; Type: TABLE; Schema: foliage_extra; Owner: foliage
--

CREATE TABLE foliage_extra.costa_laghi_502 (
    id integer NOT NULL,
    geom public.geometry(MultiPolygon,25833),
    fid integer,
    "GID" double precision,
    "OBJECTID" double precision,
    "COD_PROV_" character varying,
    "COMUNE_" character varying,
    "COD_COM_" character varying,
    "COD_REG_" character varying,
    "FID_LAGHI_" double precision,
    "NOME" character varying,
    "NOME_CTR" character varying,
    "ACCERTAMEN" character varying,
    "BUFF_DIST" double precision,
    "ID_RL" character varying,
    "ALLEGATI" character varying
);


ALTER TABLE foliage_extra.costa_laghi_502 OWNER TO foliage;

--
-- Name: costa_laghi_502_id_seq; Type: SEQUENCE; Schema: foliage_extra; Owner: foliage
--

CREATE SEQUENCE foliage_extra.costa_laghi_502_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE foliage_extra.costa_laghi_502_id_seq OWNER TO foliage;

--
-- Name: costa_laghi_502_id_seq; Type: SEQUENCE OWNED BY; Schema: foliage_extra; Owner: foliage
--

ALTER SEQUENCE foliage_extra.costa_laghi_502_id_seq OWNED BY foliage_extra.costa_laghi_502.id;


--
-- Name: costa_mare_501; Type: TABLE; Schema: foliage_extra; Owner: foliage
--

CREATE TABLE foliage_extra.costa_mare_501 (
    id integer NOT NULL,
    geom public.geometry(MultiPolygon,25833),
    fid integer,
    "ATTI" character varying,
    "NOTE_" character varying,
    "COMUNE" character varying,
    "COD_PROV" character varying,
    "COD_COM" character varying,
    "ID_RL" character varying,
    "PUA" character varying,
    "LEGGE_REG" character varying,
    "TRATTO" double precision,
    "ALLEGATI" character varying,
    "SHAPE_AREA" double precision,
    "SHAPE_LEN" double precision
);


ALTER TABLE foliage_extra.costa_mare_501 OWNER TO foliage;

--
-- Name: costa_mare_501_id_seq; Type: SEQUENCE; Schema: foliage_extra; Owner: foliage
--

CREATE SEQUENCE foliage_extra.costa_mare_501_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE foliage_extra.costa_mare_501_id_seq OWNER TO foliage;

--
-- Name: costa_mare_501_id_seq; Type: SEQUENCE OWNED BY; Schema: foliage_extra; Owner: foliage
--

ALTER SEQUENCE foliage_extra.costa_mare_501_id_seq OWNED BY foliage_extra.costa_mare_501.id;


--
-- Name: decreti_archeologici_514; Type: TABLE; Schema: foliage_extra; Owner: foliage
--

CREATE TABLE foliage_extra.decreti_archeologici_514 (
    id integer NOT NULL,
    geom public.geometry(MultiPolygon,25833),
    fid integer,
    "OBJECTID" bigint,
    "TEMATISMO" character varying,
    "NOME" character varying,
    "DATA_DECRE" timestamp without time zone,
    "DATA_PRO" timestamp without time zone,
    "DATA_GU" timestamp without time zone,
    "NUM_GU" bigint,
    "RELAZIONE" character varying,
    "SCALA" bigint,
    "CATASTO" character varying,
    "NOTE_" character varying,
    "SOPRINT" character varying,
    "TIPO_OGG" character varying,
    "NOME_AREA" character varying,
    "OPREGIONE" character varying,
    "COMUNE" character varying,
    "ID_RL" character varying,
    "VIGENTI" character varying,
    "ATTI" character varying,
    "SHAPE_LENG" double precision,
    "SHAPE_AREA" double precision
);


ALTER TABLE foliage_extra.decreti_archeologici_514 OWNER TO foliage;

--
-- Name: decreti_archeologici_514_id_seq; Type: SEQUENCE; Schema: foliage_extra; Owner: foliage
--

CREATE SEQUENCE foliage_extra.decreti_archeologici_514_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE foliage_extra.decreti_archeologici_514_id_seq OWNER TO foliage;

--
-- Name: decreti_archeologici_514_id_seq; Type: SEQUENCE OWNED BY; Schema: foliage_extra; Owner: foliage
--

ALTER SEQUENCE foliage_extra.decreti_archeologici_514_id_seq OWNED BY foliage_extra.decreti_archeologici_514.id;


--
-- Name: ex_1497_ab_518; Type: TABLE; Schema: foliage_extra; Owner: foliage
--

CREATE TABLE foliage_extra.ex_1497_ab_518 (
    id integer NOT NULL,
    geom public.geometry(MultiPolygon,25833),
    fid integer,
    "GID" double precision,
    "OBJECTID" double precision,
    "ID_RL" character varying,
    "ID_MBAC" character varying,
    "NOME" character varying,
    "DISP_TIPO" character varying,
    "DISP_DATA" timestamp without time zone,
    "GU_BU_NUM" double precision,
    "GU_BU_DATA" timestamp without time zone,
    "NOTE_" character varying,
    "ESE_DATA" timestamp without time zone,
    "ATTI_CART" character varying,
    "RIF_INT" character varying,
    "VIGENTI" character varying,
    "SHAPE_LENG" double precision,
    "ALLEGATI" character varying,
    "SHAPE_AREA" double precision
);


ALTER TABLE foliage_extra.ex_1497_ab_518 OWNER TO foliage;

--
-- Name: ex_1497_ab_518_id_seq; Type: SEQUENCE; Schema: foliage_extra; Owner: foliage
--

CREATE SEQUENCE foliage_extra.ex_1497_ab_518_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE foliage_extra.ex_1497_ab_518_id_seq OWNER TO foliage;

--
-- Name: ex_1497_ab_518_id_seq; Type: SEQUENCE OWNED BY; Schema: foliage_extra; Owner: foliage
--

ALTER SEQUENCE foliage_extra.ex_1497_ab_518_id_seq OWNED BY foliage_extra.ex_1497_ab_518.id;


--
-- Name: ex_1497_cd_519; Type: TABLE; Schema: foliage_extra; Owner: foliage
--

CREATE TABLE foliage_extra.ex_1497_cd_519 (
    id integer NOT NULL,
    geom public.geometry(MultiPolygon,25833),
    fid integer,
    "GID" double precision,
    "OBJECTID_1" double precision,
    "OBJECTID" double precision,
    "TEMATISMO" character varying,
    "COD_VINC" character varying,
    "NOME" character varying,
    "TIPO_DISP" character varying,
    "DATA_DISP" timestamp without time zone,
    "DATA_GU" timestamp without time zone,
    "NUM_GU" double precision,
    "DATA_ESE" timestamp without time zone,
    "NOTE_" character varying,
    "OPERATORE" character varying,
    "RECNO" double precision,
    "ID_RL" character varying,
    "VIGENTI" character varying,
    "ATTI" character varying,
    "MODIFICA_V" character varying,
    "ALLEGATI" character varying,
    "SHAPE_AREA" double precision,
    "SHAPE_LEN" double precision
);


ALTER TABLE foliage_extra.ex_1497_cd_519 OWNER TO foliage;

--
-- Name: ex_1497_cd_519_id_seq; Type: SEQUENCE; Schema: foliage_extra; Owner: foliage
--

CREATE SEQUENCE foliage_extra.ex_1497_cd_519_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE foliage_extra.ex_1497_cd_519_id_seq OWNER TO foliage;

--
-- Name: ex_1497_cd_519_id_seq; Type: SEQUENCE OWNED BY; Schema: foliage_extra; Owner: foliage
--

ALTER SEQUENCE foliage_extra.ex_1497_cd_519_id_seq OWNED BY foliage_extra.ex_1497_cd_519.id;


--
-- Name: geomorfologici_tipizzati_515; Type: TABLE; Schema: foliage_extra; Owner: foliage
--

CREATE TABLE foliage_extra.geomorfologici_tipizzati_515 (
    id integer NOT NULL,
    geom public.geometry(Point,25833),
    fid integer,
    "GID" double precision,
    "JOIN_COUNT" double precision,
    "ID" double precision,
    "PROPONENTE" character varying,
    "TIPOLOGIA" character varying,
    "PROVINCIA" character varying,
    "SIGLA" character varying,
    "NOME" character varying,
    "COMUNE" character varying,
    "X_COORD" double precision,
    "Y_COORD" double precision,
    "TIPO" character varying,
    "AAPP_IN_CU" character varying,
    "COD_SIC" character varying,
    "SIC_IN_CUI" character varying,
    "COD_ZPS" character varying,
    "ZPS_IN_CUI" character varying,
    "SUB_TIPO" character varying,
    "FONTE" character varying,
    "PUBBLICAZI" character varying,
    "FOTO" character varying,
    "AAPP_DI_RI" character varying,
    "SIC_DI_RIF" character varying,
    "ZPS_DI_RIF" character varying,
    "ALLEGATI" character varying,
    "NOTE" character varying,
    "ID_1" double precision,
    "FOTO_1" character varying,
    "TIPO_1" character varying,
    "AAPP_DI__1" character varying,
    "COD_SIC_1" character varying,
    "SIC_DI_R_1" character varying,
    "COD_ZPS_1" character varying,
    "ZPS_DI_R_1" character varying,
    "NOME_1" character varying,
    "COMUNE_1" character varying,
    "TIPOLOGI_1" character varying,
    "SUB_TIPO_1" character varying,
    "CATASTO" character varying,
    "PROVINCI_1" character varying,
    "FONTE_1" character varying,
    "PUBBLICA_1" character varying,
    "X_COORD_1" double precision,
    "Y_COORD_1" double precision,
    "SOURCETHM" character varying,
    "ID_RL" character varying
);


ALTER TABLE foliage_extra.geomorfologici_tipizzati_515 OWNER TO foliage;

--
-- Name: geomorfologici_tipizzati_515_id_seq; Type: SEQUENCE; Schema: foliage_extra; Owner: foliage
--

CREATE SEQUENCE foliage_extra.geomorfologici_tipizzati_515_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE foliage_extra.geomorfologici_tipizzati_515_id_seq OWNER TO foliage;

--
-- Name: geomorfologici_tipizzati_515_id_seq; Type: SEQUENCE OWNED BY; Schema: foliage_extra; Owner: foliage
--

ALTER SEQUENCE foliage_extra.geomorfologici_tipizzati_515_id_seq OWNED BY foliage_extra.geomorfologici_tipizzati_515.id;


--
-- Name: geonode:acque_pubbliche_rispetto_504_id_seq; Type: SEQUENCE; Schema: foliage_extra; Owner: foliage
--

CREATE SEQUENCE foliage_extra."geonode:acque_pubbliche_rispetto_504_id_seq"
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE foliage_extra."geonode:acque_pubbliche_rispetto_504_id_seq" OWNER TO foliage;

--
-- Name: geonode:acque_pubbliche_rispetto_504_id_seq; Type: SEQUENCE OWNED BY; Schema: foliage_extra; Owner: foliage
--

ALTER SEQUENCE foliage_extra."geonode:acque_pubbliche_rispetto_504_id_seq" OWNED BY foliage_extra.acque_pubbliche_rispetto_504.id;


--
-- Name: linee_archeologiche_508; Type: TABLE; Schema: foliage_extra; Owner: foliage
--

CREATE TABLE foliage_extra.linee_archeologiche_508 (
    id integer NOT NULL,
    geom public.geometry(MultiLineString,25833),
    fid integer,
    "GID" double precision,
    "ID_RL" character varying,
    "NOTE_" character varying,
    "TIPO" character varying,
    "NOME" character varying,
    "FONTE" character varying,
    "VINCOLO" character varying,
    "ALLEGATI" character varying,
    "SHAPE_LENG" double precision
);


ALTER TABLE foliage_extra.linee_archeologiche_508 OWNER TO foliage;

--
-- Name: linee_archeologiche_508_id_seq; Type: SEQUENCE; Schema: foliage_extra; Owner: foliage
--

CREATE SEQUENCE foliage_extra.linee_archeologiche_508_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE foliage_extra.linee_archeologiche_508_id_seq OWNER TO foliage;

--
-- Name: linee_archeologiche_508_id_seq; Type: SEQUENCE OWNED BY; Schema: foliage_extra; Owner: foliage
--

ALTER SEQUENCE foliage_extra.linee_archeologiche_508_id_seq OWNED BY foliage_extra.linee_archeologiche_508.id;


--
-- Name: mybox; Type: TABLE; Schema: foliage_extra; Owner: foliage
--

CREATE TABLE foliage_extra.mybox (
    srid integer,
    shape public.geometry
);


ALTER TABLE foliage_extra.mybox OWNER TO foliage;

--
-- Name: nat2k_habitat_prioritari_208; Type: TABLE; Schema: foliage_extra; Owner: foliage
--

CREATE TABLE foliage_extra.nat2k_habitat_prioritari_208 (
    id integer NOT NULL,
    geom public.geometry(MultiPolygon,6706),
    habitat character varying(7),
    rel_priori character varying(254),
    rel_descri character varying(254),
    rel_catego bigint,
    rel_nome_c character varying(254)
);


ALTER TABLE foliage_extra.nat2k_habitat_prioritari_208 OWNER TO foliage;

--
-- Name: nat2k_habitat_prioritari_208_id_seq; Type: SEQUENCE; Schema: foliage_extra; Owner: foliage
--

CREATE SEQUENCE foliage_extra.nat2k_habitat_prioritari_208_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE foliage_extra.nat2k_habitat_prioritari_208_id_seq OWNER TO foliage;

--
-- Name: nat2k_habitat_prioritari_208_id_seq; Type: SEQUENCE OWNED BY; Schema: foliage_extra; Owner: foliage
--

ALTER SEQUENCE foliage_extra.nat2k_habitat_prioritari_208_id_seq OWNED BY foliage_extra.nat2k_habitat_prioritari_208.id;


--
-- Name: pai_rischio_alluvione_101; Type: TABLE; Schema: foliage_extra; Owner: foliage
--

CREATE TABLE foliage_extra.pai_rischio_alluvione_101 (
    id integer,
    geom public.geometry(MultiPolygon,4326),
    cod_adb character varying,
    adb character varying,
    bacino character varying,
    rischio character varying,
    tipologia character varying,
    piano character varying,
    delibera character varying,
    data_aggiornamento character varying,
    tipo_rischio character varying,
    tr character varying,
    note character varying,
    legenda character varying,
    coord_orig character varying
);


ALTER TABLE foliage_extra.pai_rischio_alluvione_101 OWNER TO foliage;

--
-- Name: pai_rischio_frana_101; Type: TABLE; Schema: foliage_extra; Owner: foliage
--

CREATE TABLE foliage_extra.pai_rischio_frana_101 (
    id integer,
    geom public.geometry(MultiPolygon,4326),
    cod_adb character varying,
    adb character varying,
    bacino character varying,
    rischio character varying,
    tipologia character varying,
    piano character varying,
    delibera character varying,
    data_aggiornamento character varying,
    tipo_rischio character varying,
    note character varying,
    legenda character varying,
    coord_orig character varying
);


ALTER TABLE foliage_extra.pai_rischio_frana_101 OWNER TO foliage;

--
-- Name: pai_rischio_valanga_101; Type: TABLE; Schema: foliage_extra; Owner: foliage
--

CREATE TABLE foliage_extra.pai_rischio_valanga_101 (
    id integer,
    geom public.geometry(MultiPolygon,4326),
    cod_adb character varying,
    adb character varying,
    bacino character varying,
    rischio character varying,
    tipologia character varying,
    piano character varying,
    delibera character varying,
    data_aggiornamento character varying,
    tipo_rischio character varying,
    note character varying,
    legenda character varying,
    coord_orig character varying
);


ALTER TABLE foliage_extra.pai_rischio_valanga_101 OWNER TO foliage;

--
-- Name: punti_archeologici_511; Type: TABLE; Schema: foliage_extra; Owner: foliage
--

CREATE TABLE foliage_extra.punti_archeologici_511 (
    id integer NOT NULL,
    geom public.geometry(Point,25833),
    fid integer,
    "GID" double precision,
    "OBJECTID" double precision,
    "NUM_PTP" character varying,
    "NUM_TAV" character varying,
    "CLASS_AREA" character varying,
    "NOTE_" character varying,
    "ID_RL" character varying,
    "FOGLIO" character varying,
    "TIPO_OGG" character varying,
    "NOME" character varying,
    "ALLEGATI" character varying
);


ALTER TABLE foliage_extra.punti_archeologici_511 OWNER TO foliage;

--
-- Name: punti_archeologici_511_id_seq; Type: SEQUENCE; Schema: foliage_extra; Owner: foliage
--

CREATE SEQUENCE foliage_extra.punti_archeologici_511_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE foliage_extra.punti_archeologici_511_id_seq OWNER TO foliage;

--
-- Name: punti_archeologici_511_id_seq; Type: SEQUENCE OWNED BY; Schema: foliage_extra; Owner: foliage
--

ALTER SEQUENCE foliage_extra.punti_archeologici_511_id_seq OWNED BY foliage_extra.punti_archeologici_511.id;


--
-- Name: punti_archeologici_tipizzati_513; Type: TABLE; Schema: foliage_extra; Owner: foliage
--

CREATE TABLE foliage_extra.punti_archeologici_tipizzati_513 (
    id integer NOT NULL,
    geom public.geometry(MultiPoint,25833),
    fid_1 integer,
    gid bigint,
    fid double precision,
    __gid bigint,
    objectid double precision,
    tipo_ogg character varying,
    nome character varying,
    soprint character varying,
    nome_area character varying,
    comune character varying,
    note_ character varying,
    id_rl character varying,
    num_ptp character varying,
    num_tav character varying,
    class_area character varying,
    allegati character varying,
    rettifica character varying,
    funzionari character varying,
    eliminare character varying
);


ALTER TABLE foliage_extra.punti_archeologici_tipizzati_513 OWNER TO foliage;

--
-- Name: punti_archeologici_tipizzati_513_id_seq; Type: SEQUENCE; Schema: foliage_extra; Owner: foliage
--

CREATE SEQUENCE foliage_extra.punti_archeologici_tipizzati_513_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE foliage_extra.punti_archeologici_tipizzati_513_id_seq OWNER TO foliage;

--
-- Name: punti_archeologici_tipizzati_513_id_seq; Type: SEQUENCE OWNED BY; Schema: foliage_extra; Owner: foliage
--

ALTER SEQUENCE foliage_extra.punti_archeologici_tipizzati_513_id_seq OWNED BY foliage_extra.punti_archeologici_tipizzati_513.id;


--
-- Name: rispetto_geomorfologia_516; Type: TABLE; Schema: foliage_extra; Owner: foliage
--

CREATE TABLE foliage_extra.rispetto_geomorfologia_516 (
    id integer NOT NULL,
    geom public.geometry(MultiPolygon,25833),
    fid integer,
    "ID" bigint,
    "BUFFERDIST" double precision
);


ALTER TABLE foliage_extra.rispetto_geomorfologia_516 OWNER TO foliage;

--
-- Name: rispetto_geomorfologia_516_id_seq; Type: SEQUENCE; Schema: foliage_extra; Owner: foliage
--

CREATE SEQUENCE foliage_extra.rispetto_geomorfologia_516_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE foliage_extra.rispetto_geomorfologia_516_id_seq OWNER TO foliage;

--
-- Name: rispetto_geomorfologia_516_id_seq; Type: SEQUENCE OWNED BY; Schema: foliage_extra; Owner: foliage
--

ALTER SEQUENCE foliage_extra.rispetto_geomorfologia_516_id_seq OWNED BY foliage_extra.rispetto_geomorfologia_516.id;


--
-- Name: rispetto_linee_archeologiche_509; Type: TABLE; Schema: foliage_extra; Owner: foliage
--

CREATE TABLE foliage_extra.rispetto_linee_archeologiche_509 (
    id integer NOT NULL,
    geom public.geometry(MultiPolygon,25833),
    fid integer,
    "VINCOLO" character varying,
    "ALLEGATI" character varying,
    "SHAPE_LENG" double precision,
    "SHAPE_AREA" double precision
);


ALTER TABLE foliage_extra.rispetto_linee_archeologiche_509 OWNER TO foliage;

--
-- Name: rispetto_linee_archeologiche_509_id_seq; Type: SEQUENCE; Schema: foliage_extra; Owner: foliage
--

CREATE SEQUENCE foliage_extra.rispetto_linee_archeologiche_509_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE foliage_extra.rispetto_linee_archeologiche_509_id_seq OWNER TO foliage;

--
-- Name: rispetto_linee_archeologiche_509_id_seq; Type: SEQUENCE OWNED BY; Schema: foliage_extra; Owner: foliage
--

ALTER SEQUENCE foliage_extra.rispetto_linee_archeologiche_509_id_seq OWNED BY foliage_extra.rispetto_linee_archeologiche_509.id;


--
-- Name: rispetto_linee_archeologiche_tipizzate_510; Type: TABLE; Schema: foliage_extra; Owner: foliage
--

CREATE TABLE foliage_extra.rispetto_linee_archeologiche_tipizzate_510 (
    id integer NOT NULL,
    geom public.geometry(MultiPolygon,25833),
    fid integer,
    "GID" double precision,
    "OBJECTID" double precision,
    "VINCOLO" character varying,
    "SHAPE_AREA" double precision,
    "SHAPE_LEN" double precision
);


ALTER TABLE foliage_extra.rispetto_linee_archeologiche_tipizzate_510 OWNER TO foliage;

--
-- Name: rispetto_linee_archeologiche_tipizzate_510_id_seq; Type: SEQUENCE; Schema: foliage_extra; Owner: foliage
--

CREATE SEQUENCE foliage_extra.rispetto_linee_archeologiche_tipizzate_510_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE foliage_extra.rispetto_linee_archeologiche_tipizzate_510_id_seq OWNER TO foliage;

--
-- Name: rispetto_linee_archeologiche_tipizzate_510_id_seq; Type: SEQUENCE OWNED BY; Schema: foliage_extra; Owner: foliage
--

ALTER SEQUENCE foliage_extra.rispetto_linee_archeologiche_tipizzate_510_id_seq OWNED BY foliage_extra.rispetto_linee_archeologiche_tipizzate_510.id;


--
-- Name: rispetto_punti_archeologici_512; Type: TABLE; Schema: foliage_extra; Owner: foliage
--

CREATE TABLE foliage_extra.rispetto_punti_archeologici_512 (
    id integer NOT NULL,
    geom public.geometry(MultiPolygon,25833),
    fid integer,
    "GID" double precision,
    "OBJECTID" double precision,
    "BUFFERDIST" double precision,
    "ALLEGATI" character varying,
    "SHAPE_AREA" double precision,
    "SHAPE_LEN" double precision
);


ALTER TABLE foliage_extra.rispetto_punti_archeologici_512 OWNER TO foliage;

--
-- Name: rispetto_punti_archeologici_512_id_seq; Type: SEQUENCE; Schema: foliage_extra; Owner: foliage
--

CREATE SEQUENCE foliage_extra.rispetto_punti_archeologici_512_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE foliage_extra.rispetto_punti_archeologici_512_id_seq OWNER TO foliage;

--
-- Name: rispetto_punti_archeologici_512_id_seq; Type: SEQUENCE OWNED BY; Schema: foliage_extra; Owner: foliage
--

ALTER SEQUENCE foliage_extra.rispetto_punti_archeologici_512_id_seq OWNED BY foliage_extra.rispetto_punti_archeologici_512.id;


--
-- Name: siti_natura_2000_lazio_umbria; Type: TABLE; Schema: foliage_extra; Owner: foliage
--

CREATE TABLE foliage_extra.siti_natura_2000_lazio_umbria (
    id integer NOT NULL,
    geom public.geometry(MultiPolygon,6706),
    objectid_1 character varying(254),
    codice character varying(254),
    tipo_sito character varying(254),
    denominazi character varying(254),
    reg_biog character varying(254),
    regione character varying(254),
    aggiorn character varying(254),
    fuso character varying(254),
    area character varying(254),
    perimeter character varying(254),
    hectares character varying(254),
    nowprint character varying(254),
    sic_zsc character varying(254),
    zps character varying(254),
    st_area_sh character varying(254),
    st_length_ character varying(254)
);


ALTER TABLE foliage_extra.siti_natura_2000_lazio_umbria OWNER TO foliage;

--
-- Name: siti_natura_2000_lazio_umbria_id_seq; Type: SEQUENCE; Schema: foliage_extra; Owner: foliage
--

CREATE SEQUENCE foliage_extra.siti_natura_2000_lazio_umbria_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE foliage_extra.siti_natura_2000_lazio_umbria_id_seq OWNER TO foliage;

--
-- Name: siti_natura_2000_lazio_umbria_id_seq; Type: SEQUENCE OWNED BY; Schema: foliage_extra; Owner: foliage
--

ALTER SEQUENCE foliage_extra.siti_natura_2000_lazio_umbria_id_seq OWNED BY foliage_extra.siti_natura_2000_lazio_umbria.id;


--
-- Name: sitiprotetti_natura_2000; Type: TABLE; Schema: foliage_extra; Owner: foliage
--

CREATE TABLE foliage_extra.sitiprotetti_natura_2000 (
    id integer,
    geom public.geometry(MultiPolygon,4326),
    codice character varying,
    tipo_sito character varying,
    denominazi character varying,
    reg_biog character varying,
    regione character varying,
    aggiorn character varying,
    fuso character varying,
    area character varying,
    perimeter character varying,
    hectares character varying,
    nowprint character varying,
    sic_zsc character varying,
    zps character varying,
    st_area_shape_ character varying,
    st_length_shape_ character varying
);


ALTER TABLE foliage_extra.sitiprotetti_natura_2000 OWNER TO foliage;

--
-- Name: umbria_dem; Type: TABLE; Schema: foliage_extra; Owner: foliage
--

CREATE TABLE foliage_extra.umbria_dem (
    rid integer NOT NULL,
    rast public.raster,
    CONSTRAINT enforce_height_rast CHECK ((public.st_height(rast) = ANY (ARRAY[2700, 947]))),
    CONSTRAINT enforce_nodata_values_rast CHECK ((public._raster_constraint_nodata_values(rast) = '{NULL}'::numeric[])),
    CONSTRAINT enforce_num_bands_rast CHECK ((public.st_numbands(rast) = 1)),
    CONSTRAINT enforce_out_db_rast CHECK ((public._raster_constraint_out_db(rast) = '{f}'::boolean[])),
    CONSTRAINT enforce_pixel_types_rast CHECK ((public._raster_constraint_pixel_types(rast) = '{32BF}'::text[])),
    CONSTRAINT enforce_same_alignment_rast CHECK (public.st_samealignment(rast, '010000000024857CE3AD172B3F24857CE3AD172BBF21C20320129D2740D613C3E596D2454000000000000000000000000000000000E610000001000100'::public.raster)),
    CONSTRAINT enforce_scalex_rast CHECK ((round((public.st_scalex(rast))::numeric, 10) = round(0.0002066993490581838, 10))),
    CONSTRAINT enforce_scaley_rast CHECK ((round((public.st_scaley(rast))::numeric, 10) = round((- 0.0002066993490581838), 10))),
    CONSTRAINT enforce_srid_rast CHECK ((public.st_srid(rast) = 4326)),
    CONSTRAINT enforce_width_rast CHECK ((public.st_width(rast) = ANY (ARRAY[2700, 1990])))
);


ALTER TABLE foliage_extra.umbria_dem OWNER TO foliage;

--
-- Name: umbria_dem_rid_seq; Type: SEQUENCE; Schema: foliage_extra; Owner: foliage
--

CREATE SEQUENCE foliage_extra.umbria_dem_rid_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE foliage_extra.umbria_dem_rid_seq OWNER TO foliage;

--
-- Name: umbria_dem_rid_seq; Type: SEQUENCE OWNED BY; Schema: foliage_extra; Owner: foliage
--

ALTER SEQUENCE foliage_extra.umbria_dem_rid_seq OWNED BY foliage_extra.umbria_dem.rid;


--
-- Name: umbria_slope; Type: TABLE; Schema: foliage_extra; Owner: foliage
--

CREATE TABLE foliage_extra.umbria_slope (
    rid integer NOT NULL,
    rast public.raster,
    CONSTRAINT enforce_height_rast CHECK ((public.st_height(rast) = ANY (ARRAY[2700, 1192]))),
    CONSTRAINT enforce_nodata_values_rast CHECK ((public._raster_constraint_nodata_values(rast) = '{-9999.0000000000}'::numeric[])),
    CONSTRAINT enforce_num_bands_rast CHECK ((public.st_numbands(rast) = 1)),
    CONSTRAINT enforce_out_db_rast CHECK ((public._raster_constraint_out_db(rast) = '{f}'::boolean[])),
    CONSTRAINT enforce_pixel_types_rast CHECK ((public._raster_constraint_pixel_types(rast) = '{32BF}'::text[])),
    CONSTRAINT enforce_same_alignment_rast CHECK (public.st_samealignment(rast, '01000000009A182623C05F2B3F9A182623C05F2BBFBA0CA8D4F0792740953D42F7BED6454000000000000000000000000000000000E610000001000100'::public.raster)),
    CONSTRAINT enforce_scalex_rast CHECK ((round((public.st_scalex(rast))::numeric, 10) = round(0.00020884724069620137, 10))),
    CONSTRAINT enforce_scaley_rast CHECK ((round((public.st_scaley(rast))::numeric, 10) = round((- 0.00020884724069620137), 10))),
    CONSTRAINT enforce_srid_rast CHECK ((public.st_srid(rast) = 4326)),
    CONSTRAINT enforce_width_rast CHECK ((public.st_width(rast) = ANY (ARRAY[2700, 2411])))
);


ALTER TABLE foliage_extra.umbria_slope OWNER TO foliage;

--
-- Name: umbria_slope_rid_seq; Type: SEQUENCE; Schema: foliage_extra; Owner: foliage
--

CREATE SEQUENCE foliage_extra.umbria_slope_rid_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE foliage_extra.umbria_slope_rid_seq OWNER TO foliage;

--
-- Name: umbria_slope_rid_seq; Type: SEQUENCE OWNED BY; Schema: foliage_extra; Owner: foliage
--

ALTER SEQUENCE foliage_extra.umbria_slope_rid_seq OWNED BY foliage_extra.umbria_slope.rid;


--
-- Name: usi_civici_506; Type: TABLE; Schema: foliage_extra; Owner: foliage
--

CREATE TABLE foliage_extra.usi_civici_506 (
    id integer NOT NULL,
    geom public.geometry(MultiPolygon,25833),
    fid integer,
    "COMUNE" character varying,
    "SEZ_CTR" character varying,
    "NUM_PROT" double precision,
    "DATA_PROT_" double precision,
    "NOTE_" character varying,
    "ID_RL" character varying,
    "ALLEGATI" character varying,
    "SHAPE_AREA" double precision,
    "SHAPE_LEN" double precision
);


ALTER TABLE foliage_extra.usi_civici_506 OWNER TO foliage;

--
-- Name: usi_civici_506_id_seq; Type: SEQUENCE; Schema: foliage_extra; Owner: foliage
--

CREATE SEQUENCE foliage_extra.usi_civici_506_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE foliage_extra.usi_civici_506_id_seq OWNER TO foliage;

--
-- Name: usi_civici_506_id_seq; Type: SEQUENCE OWNED BY; Schema: foliage_extra; Owner: foliage
--

ALTER SEQUENCE foliage_extra.usi_civici_506_id_seq OWNED BY foliage_extra.usi_civici_506.id;


--
-- Name: zone_umide_507; Type: TABLE; Schema: foliage_extra; Owner: foliage
--

CREATE TABLE foliage_extra.zone_umide_507 (
    id integer NOT NULL,
    geom public.geometry(MultiPolygon,25833),
    fid integer,
    "TIPO" character varying,
    "AREA" double precision,
    "PERIMETER" double precision,
    "ID_RL" character varying,
    "ID_M" character varying,
    "NOME_ZONA" character varying
);


ALTER TABLE foliage_extra.zone_umide_507 OWNER TO foliage;

--
-- Name: zone_umide_507_id_seq; Type: SEQUENCE; Schema: foliage_extra; Owner: foliage
--

CREATE SEQUENCE foliage_extra.zone_umide_507_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE foliage_extra.zone_umide_507_id_seq OWNER TO foliage;

--
-- Name: zone_umide_507_id_seq; Type: SEQUENCE OWNED BY; Schema: foliage_extra; Owner: foliage
--

ALTER SEQUENCE foliage_extra.zone_umide_507_id_seq OWNED BY foliage_extra.zone_umide_507.id;


--
-- Name: acque_pubbliche_503 id; Type: DEFAULT; Schema: foliage_extra; Owner: foliage
--

ALTER TABLE ONLY foliage_extra.acque_pubbliche_503 ALTER COLUMN id SET DEFAULT nextval('foliage_extra.acque_pubbliche_503_id_seq'::regclass);


--
-- Name: acque_pubbliche_rispetto_504 id; Type: DEFAULT; Schema: foliage_extra; Owner: foliage
--

ALTER TABLE ONLY foliage_extra.acque_pubbliche_rispetto_504 ALTER COLUMN id SET DEFAULT nextval('foliage_extra."geonode:acque_pubbliche_rispetto_504_id_seq"'::regclass);


--
-- Name: altimetria_1200_505 id; Type: DEFAULT; Schema: foliage_extra; Owner: foliage
--

ALTER TABLE ONLY foliage_extra.altimetria_1200_505 ALTER COLUMN id SET DEFAULT nextval('foliage_extra.altimetria_1200_505_id_seq'::regclass);


--
-- Name: aree_paesaggistiche_215 id; Type: DEFAULT; Schema: foliage_extra; Owner: foliage
--

ALTER TABLE ONLY foliage_extra.aree_paesaggistiche_215 ALTER COLUMN id SET DEFAULT nextval('foliage_extra.aree_paesaggistiche_215_id_seq'::regclass);


--
-- Name: boschi_517 id; Type: DEFAULT; Schema: foliage_extra; Owner: foliage
--

ALTER TABLE ONLY foliage_extra.boschi_517 ALTER COLUMN id SET DEFAULT nextval('foliage_extra.boschi_517_id_seq'::regclass);


--
-- Name: costa_laghi_502 id; Type: DEFAULT; Schema: foliage_extra; Owner: foliage
--

ALTER TABLE ONLY foliage_extra.costa_laghi_502 ALTER COLUMN id SET DEFAULT nextval('foliage_extra.costa_laghi_502_id_seq'::regclass);


--
-- Name: costa_mare_501 id; Type: DEFAULT; Schema: foliage_extra; Owner: foliage
--

ALTER TABLE ONLY foliage_extra.costa_mare_501 ALTER COLUMN id SET DEFAULT nextval('foliage_extra.costa_mare_501_id_seq'::regclass);


--
-- Name: decreti_archeologici_514 id; Type: DEFAULT; Schema: foliage_extra; Owner: foliage
--

ALTER TABLE ONLY foliage_extra.decreti_archeologici_514 ALTER COLUMN id SET DEFAULT nextval('foliage_extra.decreti_archeologici_514_id_seq'::regclass);


--
-- Name: ex_1497_ab_518 id; Type: DEFAULT; Schema: foliage_extra; Owner: foliage
--

ALTER TABLE ONLY foliage_extra.ex_1497_ab_518 ALTER COLUMN id SET DEFAULT nextval('foliage_extra.ex_1497_ab_518_id_seq'::regclass);


--
-- Name: ex_1497_cd_519 id; Type: DEFAULT; Schema: foliage_extra; Owner: foliage
--

ALTER TABLE ONLY foliage_extra.ex_1497_cd_519 ALTER COLUMN id SET DEFAULT nextval('foliage_extra.ex_1497_cd_519_id_seq'::regclass);


--
-- Name: geomorfologici_tipizzati_515 id; Type: DEFAULT; Schema: foliage_extra; Owner: foliage
--

ALTER TABLE ONLY foliage_extra.geomorfologici_tipizzati_515 ALTER COLUMN id SET DEFAULT nextval('foliage_extra.geomorfologici_tipizzati_515_id_seq'::regclass);


--
-- Name: linee_archeologiche_508 id; Type: DEFAULT; Schema: foliage_extra; Owner: foliage
--

ALTER TABLE ONLY foliage_extra.linee_archeologiche_508 ALTER COLUMN id SET DEFAULT nextval('foliage_extra.linee_archeologiche_508_id_seq'::regclass);


--
-- Name: nat2k_habitat_prioritari_208 id; Type: DEFAULT; Schema: foliage_extra; Owner: foliage
--

ALTER TABLE ONLY foliage_extra.nat2k_habitat_prioritari_208 ALTER COLUMN id SET DEFAULT nextval('foliage_extra.nat2k_habitat_prioritari_208_id_seq'::regclass);


--
-- Name: punti_archeologici_511 id; Type: DEFAULT; Schema: foliage_extra; Owner: foliage
--

ALTER TABLE ONLY foliage_extra.punti_archeologici_511 ALTER COLUMN id SET DEFAULT nextval('foliage_extra.punti_archeologici_511_id_seq'::regclass);


--
-- Name: punti_archeologici_tipizzati_513 id; Type: DEFAULT; Schema: foliage_extra; Owner: foliage
--

ALTER TABLE ONLY foliage_extra.punti_archeologici_tipizzati_513 ALTER COLUMN id SET DEFAULT nextval('foliage_extra.punti_archeologici_tipizzati_513_id_seq'::regclass);


--
-- Name: rispetto_geomorfologia_516 id; Type: DEFAULT; Schema: foliage_extra; Owner: foliage
--

ALTER TABLE ONLY foliage_extra.rispetto_geomorfologia_516 ALTER COLUMN id SET DEFAULT nextval('foliage_extra.rispetto_geomorfologia_516_id_seq'::regclass);


--
-- Name: rispetto_linee_archeologiche_509 id; Type: DEFAULT; Schema: foliage_extra; Owner: foliage
--

ALTER TABLE ONLY foliage_extra.rispetto_linee_archeologiche_509 ALTER COLUMN id SET DEFAULT nextval('foliage_extra.rispetto_linee_archeologiche_509_id_seq'::regclass);


--
-- Name: rispetto_linee_archeologiche_tipizzate_510 id; Type: DEFAULT; Schema: foliage_extra; Owner: foliage
--

ALTER TABLE ONLY foliage_extra.rispetto_linee_archeologiche_tipizzate_510 ALTER COLUMN id SET DEFAULT nextval('foliage_extra.rispetto_linee_archeologiche_tipizzate_510_id_seq'::regclass);


--
-- Name: rispetto_punti_archeologici_512 id; Type: DEFAULT; Schema: foliage_extra; Owner: foliage
--

ALTER TABLE ONLY foliage_extra.rispetto_punti_archeologici_512 ALTER COLUMN id SET DEFAULT nextval('foliage_extra.rispetto_punti_archeologici_512_id_seq'::regclass);


--
-- Name: siti_natura_2000_lazio_umbria id; Type: DEFAULT; Schema: foliage_extra; Owner: foliage
--

ALTER TABLE ONLY foliage_extra.siti_natura_2000_lazio_umbria ALTER COLUMN id SET DEFAULT nextval('foliage_extra.siti_natura_2000_lazio_umbria_id_seq'::regclass);


--
-- Name: umbria_dem rid; Type: DEFAULT; Schema: foliage_extra; Owner: foliage
--

ALTER TABLE ONLY foliage_extra.umbria_dem ALTER COLUMN rid SET DEFAULT nextval('foliage_extra.umbria_dem_rid_seq'::regclass);


--
-- Name: umbria_slope rid; Type: DEFAULT; Schema: foliage_extra; Owner: foliage
--

ALTER TABLE ONLY foliage_extra.umbria_slope ALTER COLUMN rid SET DEFAULT nextval('foliage_extra.umbria_slope_rid_seq'::regclass);


--
-- Name: usi_civici_506 id; Type: DEFAULT; Schema: foliage_extra; Owner: foliage
--

ALTER TABLE ONLY foliage_extra.usi_civici_506 ALTER COLUMN id SET DEFAULT nextval('foliage_extra.usi_civici_506_id_seq'::regclass);


--
-- Name: zone_umide_507 id; Type: DEFAULT; Schema: foliage_extra; Owner: foliage
--

ALTER TABLE ONLY foliage_extra.zone_umide_507 ALTER COLUMN id SET DEFAULT nextval('foliage_extra.zone_umide_507_id_seq'::regclass);


--
-- Name: acque_pubbliche_503 acque_pubbliche_503_pkey; Type: CONSTRAINT; Schema: foliage_extra; Owner: foliage
--

ALTER TABLE ONLY foliage_extra.acque_pubbliche_503
    ADD CONSTRAINT acque_pubbliche_503_pkey PRIMARY KEY (id);


--
-- Name: altimetria_1200_505 altimetria_1200_505_pkey; Type: CONSTRAINT; Schema: foliage_extra; Owner: foliage
--

ALTER TABLE ONLY foliage_extra.altimetria_1200_505
    ADD CONSTRAINT altimetria_1200_505_pkey PRIMARY KEY (id);


--
-- Name: aree_paesaggistiche_215 aree_paesaggistiche_215_pkey; Type: CONSTRAINT; Schema: foliage_extra; Owner: foliage
--

ALTER TABLE ONLY foliage_extra.aree_paesaggistiche_215
    ADD CONSTRAINT aree_paesaggistiche_215_pkey PRIMARY KEY (id);


--
-- Name: boschi_517 boschi_517_pkey; Type: CONSTRAINT; Schema: foliage_extra; Owner: foliage
--

ALTER TABLE ONLY foliage_extra.boschi_517
    ADD CONSTRAINT boschi_517_pkey PRIMARY KEY (id);


--
-- Name: costa_laghi_502 costa_laghi_502_pkey; Type: CONSTRAINT; Schema: foliage_extra; Owner: foliage
--

ALTER TABLE ONLY foliage_extra.costa_laghi_502
    ADD CONSTRAINT costa_laghi_502_pkey PRIMARY KEY (id);


--
-- Name: costa_mare_501 costa_mare_501_pkey; Type: CONSTRAINT; Schema: foliage_extra; Owner: foliage
--

ALTER TABLE ONLY foliage_extra.costa_mare_501
    ADD CONSTRAINT costa_mare_501_pkey PRIMARY KEY (id);


--
-- Name: decreti_archeologici_514 decreti_archeologici_514_pkey; Type: CONSTRAINT; Schema: foliage_extra; Owner: foliage
--

ALTER TABLE ONLY foliage_extra.decreti_archeologici_514
    ADD CONSTRAINT decreti_archeologici_514_pkey PRIMARY KEY (id);


--
-- Name: umbria_dem enforce_max_extent_rast; Type: CHECK CONSTRAINT; Schema: foliage_extra; Owner: foliage
--

ALTER TABLE foliage_extra.umbria_dem
    ADD CONSTRAINT enforce_max_extent_rast CHECK ((public.st_envelope(rast) OPERATOR(public.@) '0103000020E6100000010000000500000021C20320129D2740C84FB5E0A92A454021C20320129D2740D613C3E596D24540ACA6B0AD27AB2A40D613C3E596D24540ACA6B0AD27AB2A40C84FB5E0A92A454021C20320129D2740C84FB5E0A92A4540'::public.geometry)) NOT VALID;


--
-- Name: umbria_slope enforce_max_extent_rast; Type: CHECK CONSTRAINT; Schema: foliage_extra; Owner: foliage
--

ALTER TABLE foliage_extra.umbria_slope
    ADD CONSTRAINT enforce_max_extent_rast CHECK ((public.st_envelope(rast) OPERATOR(public.@) '0103000020E61000000100000005000000BA0CA8D4F079274056F85F9286264540BA0CA8D4F0792740953D42F7BED645401B8018582BBD2A40953D42F7BED645401B8018582BBD2A4056F85F9286264540BA0CA8D4F079274056F85F9286264540'::public.geometry)) NOT VALID;


--
-- Name: ex_1497_ab_518 ex_1497_ab_518_pkey; Type: CONSTRAINT; Schema: foliage_extra; Owner: foliage
--

ALTER TABLE ONLY foliage_extra.ex_1497_ab_518
    ADD CONSTRAINT ex_1497_ab_518_pkey PRIMARY KEY (id);


--
-- Name: ex_1497_cd_519 ex_1497_cd_519_pkey; Type: CONSTRAINT; Schema: foliage_extra; Owner: foliage
--

ALTER TABLE ONLY foliage_extra.ex_1497_cd_519
    ADD CONSTRAINT ex_1497_cd_519_pkey PRIMARY KEY (id);


--
-- Name: geomorfologici_tipizzati_515 geomorfologici_tipizzati_515_pkey; Type: CONSTRAINT; Schema: foliage_extra; Owner: foliage
--

ALTER TABLE ONLY foliage_extra.geomorfologici_tipizzati_515
    ADD CONSTRAINT geomorfologici_tipizzati_515_pkey PRIMARY KEY (id);


--
-- Name: acque_pubbliche_rispetto_504 geonode:acque_pubbliche_rispetto_504_pkey; Type: CONSTRAINT; Schema: foliage_extra; Owner: foliage
--

ALTER TABLE ONLY foliage_extra.acque_pubbliche_rispetto_504
    ADD CONSTRAINT "geonode:acque_pubbliche_rispetto_504_pkey" PRIMARY KEY (id);


--
-- Name: linee_archeologiche_508 linee_archeologiche_508_pkey; Type: CONSTRAINT; Schema: foliage_extra; Owner: foliage
--

ALTER TABLE ONLY foliage_extra.linee_archeologiche_508
    ADD CONSTRAINT linee_archeologiche_508_pkey PRIMARY KEY (id);


--
-- Name: nat2k_habitat_prioritari_208 nat2k_habitat_prioritari_208_pkey; Type: CONSTRAINT; Schema: foliage_extra; Owner: foliage
--

ALTER TABLE ONLY foliage_extra.nat2k_habitat_prioritari_208
    ADD CONSTRAINT nat2k_habitat_prioritari_208_pkey PRIMARY KEY (id);


--
-- Name: punti_archeologici_511 punti_archeologici_511_pkey; Type: CONSTRAINT; Schema: foliage_extra; Owner: foliage
--

ALTER TABLE ONLY foliage_extra.punti_archeologici_511
    ADD CONSTRAINT punti_archeologici_511_pkey PRIMARY KEY (id);


--
-- Name: punti_archeologici_tipizzati_513 punti_archeologici_tipizzati_513_pkey; Type: CONSTRAINT; Schema: foliage_extra; Owner: foliage
--

ALTER TABLE ONLY foliage_extra.punti_archeologici_tipizzati_513
    ADD CONSTRAINT punti_archeologici_tipizzati_513_pkey PRIMARY KEY (id);


--
-- Name: rispetto_geomorfologia_516 rispetto_geomorfologia_516_pkey; Type: CONSTRAINT; Schema: foliage_extra; Owner: foliage
--

ALTER TABLE ONLY foliage_extra.rispetto_geomorfologia_516
    ADD CONSTRAINT rispetto_geomorfologia_516_pkey PRIMARY KEY (id);


--
-- Name: rispetto_linee_archeologiche_509 rispetto_linee_archeologiche_509_pkey; Type: CONSTRAINT; Schema: foliage_extra; Owner: foliage
--

ALTER TABLE ONLY foliage_extra.rispetto_linee_archeologiche_509
    ADD CONSTRAINT rispetto_linee_archeologiche_509_pkey PRIMARY KEY (id);


--
-- Name: rispetto_linee_archeologiche_tipizzate_510 rispetto_linee_archeologiche_tipizzate_510_pkey; Type: CONSTRAINT; Schema: foliage_extra; Owner: foliage
--

ALTER TABLE ONLY foliage_extra.rispetto_linee_archeologiche_tipizzate_510
    ADD CONSTRAINT rispetto_linee_archeologiche_tipizzate_510_pkey PRIMARY KEY (id);


--
-- Name: rispetto_punti_archeologici_512 rispetto_punti_archeologici_512_pkey; Type: CONSTRAINT; Schema: foliage_extra; Owner: foliage
--

ALTER TABLE ONLY foliage_extra.rispetto_punti_archeologici_512
    ADD CONSTRAINT rispetto_punti_archeologici_512_pkey PRIMARY KEY (id);


--
-- Name: siti_natura_2000_lazio_umbria siti_natura_2000_lazio_umbria_pkey; Type: CONSTRAINT; Schema: foliage_extra; Owner: foliage
--

ALTER TABLE ONLY foliage_extra.siti_natura_2000_lazio_umbria
    ADD CONSTRAINT siti_natura_2000_lazio_umbria_pkey PRIMARY KEY (id);


--
-- Name: umbria_dem umbria_dem_pkey; Type: CONSTRAINT; Schema: foliage_extra; Owner: foliage
--

ALTER TABLE ONLY foliage_extra.umbria_dem
    ADD CONSTRAINT umbria_dem_pkey PRIMARY KEY (rid);


--
-- Name: umbria_slope umbria_slope_pkey; Type: CONSTRAINT; Schema: foliage_extra; Owner: foliage
--

ALTER TABLE ONLY foliage_extra.umbria_slope
    ADD CONSTRAINT umbria_slope_pkey PRIMARY KEY (rid);


--
-- Name: usi_civici_506 usi_civici_506_pkey; Type: CONSTRAINT; Schema: foliage_extra; Owner: foliage
--

ALTER TABLE ONLY foliage_extra.usi_civici_506
    ADD CONSTRAINT usi_civici_506_pkey PRIMARY KEY (id);


--
-- Name: zone_umide_507 zone_umide_507_pkey; Type: CONSTRAINT; Schema: foliage_extra; Owner: foliage
--

ALTER TABLE ONLY foliage_extra.zone_umide_507
    ADD CONSTRAINT zone_umide_507_pkey PRIMARY KEY (id);


--
-- Name: acque_pubbliche_503_spidx; Type: INDEX; Schema: foliage_extra; Owner: foliage
--

CREATE INDEX acque_pubbliche_503_spidx ON foliage_extra.acque_pubbliche_503 USING gist (geom);


--
-- Name: acque_pubbliche_rispetto_504_spidx; Type: INDEX; Schema: foliage_extra; Owner: foliage
--

CREATE INDEX acque_pubbliche_rispetto_504_spidx ON foliage_extra.acque_pubbliche_rispetto_504 USING gist (geom);


--
-- Name: altimetria_1200_505_spidx; Type: INDEX; Schema: foliage_extra; Owner: foliage
--

CREATE INDEX altimetria_1200_505_spidx ON foliage_extra.altimetria_1200_505 USING gist (geom);


--
-- Name: aree_paesaggistiche_215_spidx; Type: INDEX; Schema: foliage_extra; Owner: foliage
--

CREATE INDEX aree_paesaggistiche_215_spidx ON foliage_extra.aree_paesaggistiche_215 USING gist (geom);


--
-- Name: aree_protette_106_spidx; Type: INDEX; Schema: foliage_extra; Owner: foliage
--

CREATE INDEX aree_protette_106_spidx ON foliage_extra.aree_protette_106 USING gist (geom);


--
-- Name: boschi_517_spidx; Type: INDEX; Schema: foliage_extra; Owner: foliage
--

CREATE INDEX boschi_517_spidx ON foliage_extra.boschi_517 USING gist (geom);


--
-- Name: costa_laghi_502_spidx; Type: INDEX; Schema: foliage_extra; Owner: foliage
--

CREATE INDEX costa_laghi_502_spidx ON foliage_extra.costa_laghi_502 USING gist (geom);


--
-- Name: costa_mare_501_spidx; Type: INDEX; Schema: foliage_extra; Owner: foliage
--

CREATE INDEX costa_mare_501_spidx ON foliage_extra.costa_mare_501 USING gist (geom);


--
-- Name: decreti_archeologici_514_spidx; Type: INDEX; Schema: foliage_extra; Owner: foliage
--

CREATE INDEX decreti_archeologici_514_spidx ON foliage_extra.decreti_archeologici_514 USING gist (geom);


--
-- Name: ex_1497_ab_518_spidx; Type: INDEX; Schema: foliage_extra; Owner: foliage
--

CREATE INDEX ex_1497_ab_518_spidx ON foliage_extra.ex_1497_ab_518 USING gist (geom);


--
-- Name: ex_1497_cd_519_spidx; Type: INDEX; Schema: foliage_extra; Owner: foliage
--

CREATE INDEX ex_1497_cd_519_spidx ON foliage_extra.ex_1497_cd_519 USING gist (geom);


--
-- Name: geomorfologici_tipizzati_515_spidx; Type: INDEX; Schema: foliage_extra; Owner: foliage
--

CREATE INDEX geomorfologici_tipizzati_515_spidx ON foliage_extra.geomorfologici_tipizzati_515 USING gist (geom);


--
-- Name: linee_archeologiche_508_spidx; Type: INDEX; Schema: foliage_extra; Owner: foliage
--

CREATE INDEX linee_archeologiche_508_spidx ON foliage_extra.linee_archeologiche_508 USING gist (geom);


--
-- Name: pai_rischio_alluvione_spidx; Type: INDEX; Schema: foliage_extra; Owner: foliage
--

CREATE INDEX pai_rischio_alluvione_spidx ON foliage_extra.pai_rischio_alluvione_101 USING gist (geom);


--
-- Name: pai_rischio_frana_spidx; Type: INDEX; Schema: foliage_extra; Owner: foliage
--

CREATE INDEX pai_rischio_frana_spidx ON foliage_extra.pai_rischio_frana_101 USING gist (geom);


--
-- Name: punti_archeologici_511_spidx; Type: INDEX; Schema: foliage_extra; Owner: foliage
--

CREATE INDEX punti_archeologici_511_spidx ON foliage_extra.punti_archeologici_511 USING gist (geom);


--
-- Name: punti_archeologici_tipizzati_513_spidx; Type: INDEX; Schema: foliage_extra; Owner: foliage
--

CREATE INDEX punti_archeologici_tipizzati_513_spidx ON foliage_extra.punti_archeologici_tipizzati_513 USING gist (geom);


--
-- Name: rispetto_geomorfologia_516_spidx; Type: INDEX; Schema: foliage_extra; Owner: foliage
--

CREATE INDEX rispetto_geomorfologia_516_spidx ON foliage_extra.rispetto_geomorfologia_516 USING gist (geom);


--
-- Name: rispetto_linee_archeologiche_509_spidx; Type: INDEX; Schema: foliage_extra; Owner: foliage
--

CREATE INDEX rispetto_linee_archeologiche_509_spidx ON foliage_extra.rispetto_linee_archeologiche_509 USING gist (geom);


--
-- Name: rispetto_linee_archeologiche_tipizzate_510_spidx; Type: INDEX; Schema: foliage_extra; Owner: foliage
--

CREATE INDEX rispetto_linee_archeologiche_tipizzate_510_spidx ON foliage_extra.rispetto_linee_archeologiche_tipizzate_510 USING gist (geom);


--
-- Name: rispetto_punti_archeologici_512_spidx; Type: INDEX; Schema: foliage_extra; Owner: foliage
--

CREATE INDEX rispetto_punti_archeologici_512_spidx ON foliage_extra.rispetto_punti_archeologici_512 USING gist (geom);


--
-- Name: siti_natura_2000_lazio_umbria_spidx; Type: INDEX; Schema: foliage_extra; Owner: foliage
--

CREATE INDEX siti_natura_2000_lazio_umbria_spidx ON foliage_extra.siti_natura_2000_lazio_umbria USING gist (geom);


--
-- Name: sitiprotetti_natura_2000_spidx; Type: INDEX; Schema: foliage_extra; Owner: foliage
--

CREATE INDEX sitiprotetti_natura_2000_spidx ON foliage_extra.sitiprotetti_natura_2000 USING gist (geom);


--
-- Name: umbria_dem_st_convexhull_idx; Type: INDEX; Schema: foliage_extra; Owner: foliage
--

CREATE INDEX umbria_dem_st_convexhull_idx ON foliage_extra.umbria_dem USING gist (public.st_convexhull(rast));


--
-- Name: umbria_slope_st_convexhull_idx; Type: INDEX; Schema: foliage_extra; Owner: foliage
--

CREATE INDEX umbria_slope_st_convexhull_idx ON foliage_extra.umbria_slope USING gist (public.st_convexhull(rast));


--
-- Name: usi_civici_506_spidx; Type: INDEX; Schema: foliage_extra; Owner: foliage
--

CREATE INDEX usi_civici_506_spidx ON foliage_extra.usi_civici_506 USING gist (geom);


--
-- Name: zone_umide_507_spidx; Type: INDEX; Schema: foliage_extra; Owner: foliage
--

CREATE INDEX zone_umide_507_spidx ON foliage_extra.zone_umide_507 USING gist (geom);


--
-- PostgreSQL database dump complete
--

