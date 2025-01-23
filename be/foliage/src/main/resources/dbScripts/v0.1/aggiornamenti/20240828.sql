
alter table foliage2.flgbatch_ondemand_tab add data_avvio timestamp without time zone;

update foliage2.flgbatch_ondemand_tab 
set data_avvio = data_rife;

---------

update foliage2.flgparticella_forestale_shape_tab
set shape = st_transform(st_setsrid(shape, 3857), 3035);

update foliage2.flgvincoli_ista_tab
set shape = st_transform(st_setsrid(shape, 3857), 3035);

update foliage2.flgviabilita_ista_tab
set shape = st_transform(st_setsrid(shape, 3857), 3035);

update foliage2.flgunita_omogenee_tab
set shape = st_transform(st_setsrid(shape, 3857), 3035);

update foliage2.flgstrati_ista_tab
set shape = st_transform(st_setsrid(shape, 3857), 3035);

---------

update foliage2.flgparticella_forestale_shape_tab
set superficie = st_area(shape);

update foliage2.flgvincoli_ista_tab
set superficie = st_area(shape);

update foliage2.flgunita_omogenee_tab
set superficie = st_area(shape),
	superficie_utile = st_area(shape) - (superficie_aree_improduttive + superficie_chiare_radure + superficie_aree_interdette);
	
update foliage2.flgstrati_ista_tab
set superficie_strato = st_area(shape);

