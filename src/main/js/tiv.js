!function (d) {
  const style = d.createElement('style')
    style.innerText = '\
.__tiv__button {\
  position: fixed;\
  top: 0px;\
  left: 0px;\
  z-index: 99999;\
}\
.__tiv__dialog {\
  position: fixed;\
  z-index: 99999;\
}\
.__tiv__img {\
  opacity: 0.5;\
  position: absolute;\
  top: 0px;\
  left: 0px;\
  z-index: 99999;\
}\
@media only screen and (min-width: $modal-width) {\
  .__tiv__dialog {\
      left: 50%;\
      margin-left: -#{$modal-width / 2};\
  }\
}\
@media only screen and (min-height: $modal-height) {\
  .__tiv__dialog {\
      top: 50%;\
      margin-top: -#{$modal-height / 2};\
  }\
}\
    '
  d.body.append(style)

  const prepareDialog = () => {
    const c = d.createElement('div')
    c.classList.add('__tiv__dialog')

    const toDraggable = (img) => {
      img.classList.add("__tiv__img")

      let isDragging = false
      let ox = 0, oy = 0
      let rect = img.getBoundingClientRect()
      img.addEventListener('mousedown', e => {
        ox = e.offsetX
        oy = e.offsetY
        isDragging = true
      })
      img.addEventListener('mousemove', e => {
        if (isDragging) {
          img.style.left = `${e.clientX - ox}px`
          img.style.top = `${e.clientY - oy}px`
        }
      })
      img.addEventListener('mouseup', e => {
        isDragging = false
      })
      img.addEventListener('mouseleave', e => {
        isDragging = false
      })
      img.addEventListener('dblclick', e => {
        console.log(e)
      })
    }

    const file = d.createElement('input')
    file.type = 'file'
    file.addEventListener('change', () => {
      const img = new Image()
      img.onload = () => {
        const div = d.createElement('div')
        toDraggable(div)
        div.style.width = `${img.naturalWidth}px`
        div.style.height = `${img.naturalHeight}px`
        div.style.backgroundImage = `url(${img.src})`
        d.body.append(div)
      }
      img.src = URL.createObjectURL(file.files[0])
    })

    c.appendChild(file)
  
    return c
  }

  const p = prepareDialog()
  p.hidden = true
  d.body.append(p)

  const btn = d.createElement('button')
  btn.type = 'button'
  btn.textContent = '@'
  btn.classList.add('__tiv__button')
  btn.addEventListener('click', () => {
    p.querySelector('input[type="file"]').click()
  })
  d.body.append(btn)
  
}(document);
