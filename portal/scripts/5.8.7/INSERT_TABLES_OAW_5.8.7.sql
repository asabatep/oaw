insert into tguidelines(cod_guideline,des_guideline) values (12, 'observatorio-une-en2019-nobroken.xml');
insert into tguidelines(cod_guideline,des_guideline) values (13, 'observatorio-une-en2019_pdf-nobroken.xml');

insert into cartucho(nombre, instalado, aplicacion,numrastreos,numhilos,id_guideline) values ('es.inteco.accesibilidad.CartuchoAccesibilidad',1,'UNE-EN301549:2019_NO_BROKEN_LNK',15,50,12);
insert into cartucho(nombre, instalado, aplicacion,numrastreos,numhilos,id_guideline) values ('es.inteco.accesibilidad.CartuchoAccesibilidad',1,'UNE-EN-2019-PDF_NO_BROKEN_LNK',15,50,13);

insert into usuario_cartucho (id_usuario, id_cartucho) values (1, 12);
insert into usuario_cartucho (id_usuario, id_cartucho) values (1, 13);
