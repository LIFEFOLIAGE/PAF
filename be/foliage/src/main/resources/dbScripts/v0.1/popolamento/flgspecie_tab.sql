insert into FOLIAGE2.FLGSPECIE_TAB(TIPO_SOPRASUOLO, NOME_SPECIE, NOME_SCENTIFICO)
select --SPLIT_PART(T.line, '	', 1)::int as id_specie,
	SPLIT_PART(T.line, '	', 2) as tipo_soprasuolo,
	SPLIT_PART(T.line, '	', 3) as nome,
	SPLIT_PART(T.line, '	', 4) as nome_scentifico
from unnest(
		STRING_TO_ARRAY(
'1	Latifoglia	Acero campestre	Acer campestre
2	Latifoglia	Acero di monte	Acer pseudoplatanus
3	Latifoglia	Acero d''Ungheria	Acer opalus obtusatum
4	Latifoglia	Betulla	Betula pendula
5	Latifoglia	Carpino bianco	Carpinus betulus
6	Latifoglia	Carpino nero	Ostrya carpinifolia
7	Latifoglia	Castagno	Castanea sativa
8	Latifoglia	Cerro	Quercus cerris
9	Latifoglia	Ciliegio selvatico	Prunus avium
10	Conifera	Cipresso	Cupressus spp
11	Latifoglia	Corbezzolo	Arbutus unedo
12	Latifoglia	Eucalipto spp	Eucalyptus spp
13	Latifoglia	Faggio	Fagus sylvatica
14	Latifoglia	Farnetto	Quercus frainetto
15	Latifoglia	Farnia	Quercus robur
16	Latifoglia	Fragno	Quercus trojana
17	Latifoglia	Frassino maggiore	Fraxinus excelsior
18	Latifoglia	Ilatro comune	Phyllirea latifolia
19	Latifoglia	Leccio	Quercus ilex
20	Latifoglia	Nocciolo	Corylus avellana
21	Latifoglia	Olmo comune	Ulmus minor
22	Latifoglia	Ontano napoletano	Alnus cordata
23	Latifoglia	Ontano nero	Alnus glutinosa
24	Latifoglia	Orniello	Fraxinus ornus
25	Conifera	Pino domestico	Pinus pinea
26	Conifera	Pino d''Aleppo	Pinus halepensis
27	Conifera	Pino laricio	Pinus nigra laricio
28	Conifera	Pino marittimo	Pinus pinaster
29	Conifera	Pino nero	Pinus nigra
30	Latifoglia	Pioppo nero	Populus nigra
31	Latifoglia	Pioppo tremulo	Populus tremula
32	Latifoglia	Robinia	Robinia pseudoacacia
33	Latifoglia	Roverella	Quercus pubescens
34	Latifoglia	Rovere	Quercus petraea
35	Latifoglia	Salicone	Salix caprea
36	Latifoglia	Tiglio selvatico	Tilia cordata
37	Latifoglia	Sughera	Quercus suber',
'
'
		)
	) as T(line);
