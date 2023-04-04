@tagSmocktets
Feature: quiero buscar ciudad capital por código ISO del país
        Yo como usuario del servicio quiero consultar la capital de un país por su código ISO

  Scenario Outline: busqueda con con error en el servidor
    When el usuario ejecute la peticion con código "<isoCoddeCiudad>"  para buscar una ciudad capital
    Then el usuario debería obtener el nombre de ciudad "<capitalCiudad>"

    Examples: 
      | isoCoddeCiudad | capitalCiudad                     |
      | BB             | Bridgetown                        |
      | BW             | Gaborone                          |
      | shghj56        | Country not found in the database |

  Scenario: busqueda sin coincidencias
    When el usuario ejecute la petición con codigo "&%_ddd"  para buscar una ciudad capital
    Then el sistema no encontrará ninguna ciudad y la respuesta será "Server Error"
