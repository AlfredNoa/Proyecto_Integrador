// El menú sigue disponible cuando JavaScript está desactivado.
const menu = document.querySelector('.side'), toggle = menu?.querySelector('.navtoggle');
if (toggle) {
  menu.classList.add('enhanced'); toggle.hidden = false;
  const setOpen = open => {menu.classList.toggle('open', open); toggle.setAttribute('aria-expanded', String(open));};
  toggle.addEventListener('click', () => setOpen(!menu.classList.contains('open')));
  menu.addEventListener('keydown', e => {if(e.key === 'Escape' && menu.classList.contains('open')) {setOpen(false); toggle.focus();}});
}
