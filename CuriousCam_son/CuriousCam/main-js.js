function mesajKutusu(baslik, mesaj) {
	document.getElementById("modal_baslik").innerHTML = baslik;
	document.getElementById("modal_mesaj").innerHTML = mesaj;
	document.getElementById("modal_btn").click();
}