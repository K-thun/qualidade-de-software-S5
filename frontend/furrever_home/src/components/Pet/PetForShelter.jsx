import React, { useEffect, useState } from 'react'
import { useLocation } from 'react-router-dom'
import PetDetail from './PetDetail'
import ShelterDetail from './ShelterDetail'
import { toast } from "react-toastify"
import { getPetById } from '../../service/api/shelterService'

const PetForShelter = () => {
    const location = useLocation();
    const petId = location.state.id;
    const [pet,setPet] = useState({
      type:"",
      breed:"",
      birthDate:"",
      gender:"",
      petMedicalHistory:"",
      color:"",
      image:"",
      petId:"",
      adopted:""
    })
    const [shelter,setShelter] = useState({
      name:"",
      address:"",
      city:"",
      country:"",
      contact:"",
    });
    const [vaccine,setVaccine] = useState([])

    useEffect(() =>{
      getPetById(petId)
      .then(response => {

        const DOB = response.data.birthdate.substring(0,10)
        setPet({
          type:response.data.type,
          breed:response.data.breed,
          birthdate:DOB,
          gender:response.data.gender,
          colour:response.data.colour,
          petMedicalHistory:response.data.petMedicalHistory,
          petImage:response.data.petImage,
          petID:response.data.petID,
          adopted:response.data.adopted
        })

        const res = response.data.shelter

        setShelter({
          name:res.name,
          address:res.address,
          city:res.city,
          country:res.country,
          contact:res.contact
        })
        setVaccine(response.data.vaccineNameList)
      })
      .catch(error => {
        toast.error("Cannot get pet details")
      })

    },[])



  return (

    <div className="w-full py-6 space-y-6 bg-gray-100">
      <div className="container mx-auto py-8">
        <div className="grid gap-6 grid-cols-2">
          <PetDetail pet={pet} petId={petId} />
          <ShelterDetail shelter={shelter} vaccine={vaccine}  petId={petId}/>
        </div>
      </div>
    </div>
  )
}

export default PetForShelter
