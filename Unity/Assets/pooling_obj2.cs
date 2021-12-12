using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class pooling_obj2 : MonoBehaviour
{
    public GameObject hat;
    // Start is called before the first frame update
    void Start()
    {
    }

    // Update is called once per frame
    void Update()
    {
        if (Input.GetKeyDown(KeyCode.H))
        {
            GameObject instance = Instantiate(hat, transform.position, transform.rotation) as GameObject;
            instance.transform.localScale = new Vector3(0.7f, 0.7f, 0.7f);
        }
    }
}
