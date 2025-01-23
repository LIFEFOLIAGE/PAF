insert into FOLIAGE2.FLGSOTTOCATEGORIE_TAB(id_categoria, nome_sottocategoria)
select id_categoria, nome_sottocategoria
from (
		select SPLIT_PART(T.line, '	', 1)::int as cod_categoria,
			SPLIT_PART(T.line, '	', 2)::int as id_sottocat,
			SPLIT_PART(T.line, '	', 3) as nome_sottocategoria
		from unnest(
				STRING_TO_ARRAY(
'1	1	Altre formazioni di larice e cembro
1	2	Lariceto in fustaia chiusa
1	3	Larici isolati nella brughiera subalpina
1	4	Larici-cembreto
2	5	Altre formazioni con prevalenza di peccio
2	6	Pecceta montana
2	7	Pecceta subalpina
3	8	Abetina a Campanula
3	9	Abetina a Cardamine
3	10	Abetina e abeti-faggeta a Vaccinium e Maianthemum
3	11	Altre formazioni di abete bianco
4	12	Altre formazioni a pino silvestre e pino montano
4	13	Pineta (pino silveste) a carice oppure astragali
4	14	Pineta (pino silveste) a erica
4	15	Pineta (pino silveste) a farnia e molinia
4	16	Pineta (pino silveste) a roverella e citiso a foglie sessili
4	17	Pineta di pino montano
5	18	Altre formazioni a pino nero e pino loricato
5	19	Pineta a pino nero a citiso e ginestra
5	20	Pineta a pino nero a erica e orniello
5	21	Pineta a pino nero a pino laricio (Pinus laricio)
5	22	Pineta a pino nero a pino loricato (Pinus leucodermis)
6	23	Pinete a Pinus halepensis
6	24	Pinete a Pinus pinaster
6	25	Pinete a Pinus pinea
7	26	Altre formazioni a conifere
7	27	Formazioni a cipresso
8	28	Altre formazioni di faggio
8	29	Faggete a agrifoglio, felci e campanula
8	30	Faggete acidofile a Luzula
8	31	Faggete mesofile
8	32	Faggete termofile a Cephalanthera
9	33	Altre formazioni di rovere, roverella e farnia
9	34	Boschi di farnia
9	35	Boschi di rovere
9	36	Boschi di roverella
10	37	Altre formazioni di cerro, farnetto, fragno o vallonea
10	38	Boschi di farnetto
10	39	Boschi di fragno e nuclei di vallonea
10	40	Cerrete collinari e montane
10	41	Cerrete di pianura
11	42	Castagneti da frutto, selve castanili
11	43	Castagneti da legno
12	44	Boscaglia a carpino orientale
12	45	Boschi di carpino bianco
12	46	Boschi di carpino nero e orniello
13	47	Altre formazioni forestali in ambienti umidi
13	48	Boschi a frassino ossifillo e olmo
13	49	Boschi a ontano bianco
13	50	Boschi a ontano nero
13	51	Pioppeti naturali
13	52	Plataneto
13	53	Saliceti ripariali
14	54	Acereti appenninici
14	55	Acero-tilieti di monte e boschi di frassino ecc.
14	56	Altre formazioni caducifoglie
14	57	Betuleti, boschi montani pioneri
14	58	Boscaglie di Cercis
14	59	Boschi di ontano napoletano
14	60	Robineti e ailanteti
15	61	Boscaglia di leccio
15	62	Bosco misto di leccio e orniello
15	63	Lecceta rupicola
15	64	Lecceta termofila costiera
16	65	Pascolo arborato di sughera
16	66	Sugherete mediterranee
17	67	Boscaglie termo-mediterranee
17	68	Boschi sempreverdi di ambienti umidi
18	69	Pioppeti artificiali
19	70	Piantagioni di eucalipti
19	71	Piantagioni di latifoglie
20	72	Altre piantagioni di conifere esotiche
20	73	Piantagioni di conifere indigene
20	74	Pinus radiata
20	75	Pseudotsuga menziesii
21	76	Altri arbusteti subalpini di aghifoglie
21	77	Brughiera subalpina
21	78	Formazione ad ontano verde
21	79	Mughete
21	80	Saliceti alpini
22	81	Altre formazioni a ginestre
22	82	Altri arbusteti di clima temperato
22	83	Arbusteti a ginepro
22	84	Arbusteti a ginestra (Spartium junceum)
22	85	Arbusteti a ginestra dell''Etna (Genista aetnensis)
22	86	Pruneti e corileti
23	87	Altri arbusteti sempreverdi
23	88	Cisteti
23	89	Formazioni a ginepri sul litorale
23	90	Macchia a lentisco
23	91	Macchia litorale',
'
'
				)
			) as T(line)
	) as T
	join FOLIAGE2.FLGCATEGORIE_TAB using (cod_categoria);

